package ua.com.lavi.komock.engine

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.handler.AfterRequestHandler
import ua.com.lavi.komock.engine.handler.BeforeRequestHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.RequestHandler
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.AnyRequest
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.util.regex.Pattern


/**
 * Class represents logic for request performing
 * Created by Oleksandr Loushkin
 */

class RequestHandlerBuilder(val routeProperties: RouteProperties) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val parameterRegexp = Pattern.compile("\\$\\{(.+?)}")

    fun beforeRouteHandler(): BeforeRequestHandler {

        val beforeRequestHandler = object : BeforeRequestHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logRequest) {
                    log.info("requestURL: ${request.httpServletRequest().requestURL}. requestBody: ${request.requestBody()}")
                }
                if (routeProperties.logBefore.isNotEmpty()) {
                    log.info(routeProperties.logBefore)
                }
            }
        }
        return beforeRequestHandler
    }

    fun afterRequestHandler(): AfterRequestHandler {

        val afterRequestHandler = object : AfterRequestHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logResponse) {
                    log.info("requestURL: ${request.httpServletRequest().requestURL}. responseBody: ${response.content}")
                }

                if (routeProperties.logAfter.isNotEmpty()) {
                    log.info(routeProperties.logAfter)
                }
            }
        }
        return afterRequestHandler
    }

    fun routeHandler(): RequestHandler {

        val requestHandler = object : RequestHandler {
            override fun handle(request: Request, response: Response) {

                //if enabled headerAuth property. request should contains a header with appropriate header
                if (routeProperties.headerAuth.enabled) {
                    val headerValue = request.httpServletRequest().getHeader(routeProperties.headerAuth.name)
                    if (routeProperties.headerAuth.value != headerValue) {
                        response.statusCode(401)
                        return
                    }
                }

                response.contentType(routeProperties.contentType)
                response.statusCode(routeProperties.code)
                response.content = replacePlaceholders(request.queryParametersMap(), routeProperties.responseBody)

                // add http headers
                routeProperties.responseHeaders.forEach { header -> response.addHeader(header.key, header.value) }

                //add cookies
                routeProperties.cookies.forEach { cookie -> response.addCookie(cookie) }

                //response delay
                if (routeProperties.delay > 0) {
                    Thread.sleep(routeProperties.delay)
                }
            }
        }
        return requestHandler

    }

    /**
     * Invoke http call by apache http client
     */
    fun callbackHandler(): CallbackHandler {

        val requestHandler = object : CallbackHandler {
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
                            val response: CloseableHttpResponse = httpclient.execute(anyRequest)
                            log.info("Request to: {}. Got response: {}", callbackProperties.url, response.statusLine.toString())
                        } catch (t: Throwable) {
                            log.warn(t.message)
                        }
                    }
                }
            }
        }
        return requestHandler

    }

    /**
     * Replace response body text by parameters from the http request testP: blabla and someElse: abc
     * Example body source: Here is the parameter ${testP} and other ${someElse}
     * Example body response: Here is the parameter blabla and other abc
     */
    fun replacePlaceholders(parametersMap: Map<String, String>, str: String): String {
        val matcher = parameterRegexp.matcher(str)
        val sb = StringBuffer()
        while (matcher.find()) {
            val value = parametersMap[matcher.group(1)]
            if (value != null) {
                matcher.appendReplacement(sb, value)
            }
        }
        matcher.appendTail(sb)
        return sb.toString()
    }
}
