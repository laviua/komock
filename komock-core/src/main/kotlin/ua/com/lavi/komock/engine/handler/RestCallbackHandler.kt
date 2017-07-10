package ua.com.lavi.komock.engine.handler

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.AnyRequest
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class RestCallbackHandler(val routeProperties: RouteProperties) : CallbackHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(request: Request, response: Response) {
        if (routeProperties.callback.enabled) {

            //callback request will be invoked in the another thread
            launch(CommonPool) {
                val callbackProperties = routeProperties.callback

                val httpclient = HttpClients.createMinimal()
                val anyRequest = AnyRequest(callbackProperties.httpMethod, callbackProperties.url)
                val requestConfig = RequestConfig.custom()
                        .setConnectTimeout(callbackProperties.connectTimeout)
                        .setConnectionRequestTimeout(callbackProperties.connectionRequestTimeout)
                        .setSocketTimeout(callbackProperties.socketTimeout)
                        .build()

                // add body to the request. it needs for the POST callback
                if (callbackProperties.requestBody.isNotBlank()) {
                    anyRequest.entity = ByteArrayEntity(callbackProperties.requestBody.toByteArray(Charsets.UTF_8))
                }
                callbackProperties.requestHeaders.forEach { header -> anyRequest.addHeader(header.key, header.value) }
                anyRequest.config = requestConfig

                //perform request and log if something went wrong
                try {
                    val httpResponse: CloseableHttpResponse = httpclient.execute(anyRequest)
                    log.info("Request to: {}. Got response: {}", callbackProperties.url, httpResponse.statusLine.toString())
                } catch (t: Throwable) {
                    log.warn(t.message)
                }
            }
        }
    }
}