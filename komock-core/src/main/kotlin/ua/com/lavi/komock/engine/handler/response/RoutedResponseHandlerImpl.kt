package ua.com.lavi.komock.engine.handler.response

import ua.com.lavi.komock.engine.VariableResolver
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
open class RoutedResponseHandlerImpl(val routeProperties: RouteProperties) : ResponseHandler {

    override fun handle(request: Request, response: Response) {

        //if enabled headerAuth property. request should contains a header with appropriate header
        if (routeProperties.headerAuth.enabled) {
            val headerValue = request.httpServletRequest().getHeader(routeProperties.headerAuth.name)
            if (routeProperties.headerAuth.value != headerValue) {
                response.code(401)
                return
            }
        }

        response.contentType(routeProperties.contentType)
        response.code(routeProperties.code)
        response.content = VariableResolver.resolve(request.queryParametersMap(), routeProperties.responseBody)

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