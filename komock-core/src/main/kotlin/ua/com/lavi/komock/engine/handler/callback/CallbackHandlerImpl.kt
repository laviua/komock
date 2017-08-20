package ua.com.lavi.komock.engine.handler.callback

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.CallbackProperties
import ua.com.lavi.komock.engine.model.config.http.CallbackRequest
import kotlin.concurrent.thread


/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class CallbackHandlerImpl(private val callbackProperties: CallbackProperties) : CallbackHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(request: Request, response: Response) {
        if (callbackProperties.enabled) {

            //callback request will be invoked in the another thread
            thread {
                //delay before callback
                if (callbackProperties.delay > 0) {
                    log.info("Delay before callback request: ${callbackProperties.delay} ms")
                    Thread.sleep(callbackProperties.delay)
                }
                val httpclient = HttpClients.custom()
                        .setSSLContext(SSLContextBuilder()
                                .loadTrustMaterial(null) { _, _ -> true }.build())
                        .setSSLHostnameVerifier(NoopHostnameVerifier())
                        .build()

                // add body to the request. it needs for the POST callback
                val callbackRequest = callbackRequest(callbackProperties)
                //perform request and log if something went wrong
                try {
                    val httpResponse: CloseableHttpResponse = httpclient.execute(callbackRequest)
                    log.info("Request to: {}. Got response: {}", callbackProperties.url, httpResponse.statusLine.toString())
                } catch (t: Throwable) {
                    log.warn("Error", t)
                }
                finally {
                    callbackRequest.releaseConnection()
                }
            }
        }
    }


    private fun callbackRequest(callbackProperties: CallbackProperties) : CallbackRequest {
        val anyRequest = CallbackRequest(callbackProperties.httpMethod, callbackProperties.url)
        callbackProperties.requestHeaders.forEach { header -> anyRequest.addHeader(header.key, header.value) }
        if (callbackProperties.requestBody.isNotBlank()) {
            anyRequest.entity = ByteArrayEntity(callbackProperties.requestBody.toByteArray(Charsets.UTF_8))
        }
        anyRequest.config = RequestConfig.custom()
                .setConnectTimeout(callbackProperties.connectTimeout)
                .setConnectionRequestTimeout(callbackProperties.connectionRequestTimeout)
                .setSocketTimeout(callbackProperties.socketTimeout)
                .build()
        return anyRequest
    }
}