package ua.com.lavi.komock.engine.handler

import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.util.regex.Pattern

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class ResponseBodyHandler(val routeProperties: RouteProperties): ResponseHandler {

    private val parameterRegexp = Pattern.compile("\\$\\{(.+?)}")

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