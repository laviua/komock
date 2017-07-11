package ua.com.lavi.komock.engine.handler

import ua.com.lavi.komock.engine.VariableResolver
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class ResponseBodyHandler(val routeProperties: RouteProperties): ResponseHandler {

    private val templateResolver: VariableResolver = VariableResolver()

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
        response.content = templateResolver.resolve(request.queryParametersMap(), routeProperties.responseBody)

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