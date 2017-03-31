package ua.com.lavi.komock.engine


import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.handler.AfterRequestHandler
import ua.com.lavi.komock.engine.handler.BeforeRequestHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.RequestHandler
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.AnyRequest
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.lang.Exception
import java.util.regex.Pattern


/**
 * Created by Oleksandr Loushkin
 */

class RequestHandlerBuilder(val routeProperties: RouteProperties) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val parameterRegexp = Pattern.compile("\\$\\{(.+?)}")

    fun beforeRouteHandler(): BeforeRequestHandler {

        val beforeRequestHandler = object : BeforeRequestHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logRequest) {
                    log.info("url: ${routeProperties.url}. RequestBody: ${request.requestBody()}")
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
                    log.info("url: ${routeProperties.url}. ResponseBody: ${response.content}")
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

                //if enabled property. request should contains a header with appropriate header
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
            }
        }
        return requestHandler

    }

    fun replacePlaceholders(parametersMap: Map<String, String>, str: String): String {
        val m = parameterRegexp.matcher(str)
        val sb = StringBuffer()
        while (m.find()) {
            val value = parametersMap[m.group(1)]
            if (value != null) {
                m.appendReplacement(sb, value)
            }
        }
        m.appendTail(sb)
        return sb.toString()
    }

    fun callbackHandler(): CallbackHandler {

        val requestHandler = object : CallbackHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.callback.enabled) {
                    val callbackProperties = routeProperties.callback

                    val httpclient = HttpAsyncClients.createDefault()
                    httpclient.use { httpclient ->
                        httpclient.start()
                        val request = AnyRequest(callbackProperties.httpMethod, callbackProperties.url)
                        callbackProperties.requestHeaders.forEach { header -> request.addHeader(header.key, header.value) }
                        httpclient.execute(request, object : FutureCallback<HttpResponse> {
                            override fun failed(ex: Exception?) {
                                log.info("Callback request is failed. ${ex!!.message}")
                            }

                            override fun completed(result: HttpResponse?) {
                                log.info("Callback request is successfully completed")
                            }

                            override fun cancelled() {
                                log.info("Callback request is cancelled")
                            }

                        })
                        log.info("Callback sent to: ${callbackProperties.url}")
                    }


                }
            }
        }
        return requestHandler

    }
}
