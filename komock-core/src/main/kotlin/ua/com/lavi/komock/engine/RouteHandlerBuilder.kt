package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.handler.AfterRouteHandler
import ua.com.lavi.komock.engine.handler.BeforeRouteHandler
import ua.com.lavi.komock.engine.handler.RouteHandler
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.util.regex.Pattern

/**
 * Created by Oleksandr Loushkin
 */

class RouteHandlerBuilder(val routeProperties: RouteProperties) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val parameterRegexp = Pattern.compile("\\$\\{(.+?)}")

    fun beforeRouteHandler(): BeforeRouteHandler {

        val beforeRouteHandler = object : BeforeRouteHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logRequest) {
                    log.info("url: ${routeProperties.url}. RequestBody: ${request.requestBody()}")
                }
                if (routeProperties.logBefore.isNotEmpty()) {
                    log.info(routeProperties.logBefore)
                }
            }
        }
        return beforeRouteHandler
    }

    fun afterRouteHandler(): AfterRouteHandler {

        val afterRouteHandler = object : AfterRouteHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logResponse) {
                    log.info("url: ${routeProperties.url}. ResponseBody: ${response.content}")
                }

                if (routeProperties.logAfter.isNotEmpty()) {
                    log.info(routeProperties.logAfter)
                }
            }
        }
        return afterRouteHandler
    }

    fun routeHandler(): RouteHandler {

        val routeHandler = object : RouteHandler {
            override fun handle(request: Request, response: Response) {
                response.contentType(routeProperties.contentType)
                response.statusCode(routeProperties.code)
                response.content = replacePlaceholders(request.queryParametersMap(), routeProperties.responseBody)

                routeProperties.responseHeaders.forEach {
                    it.forEach {
                        response.addHeader(it.key, it.value)
                    }
                }

                routeProperties.cookies.forEach {
                    response.addCookie(it)
                }
            }
        }
        return routeHandler

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

}
