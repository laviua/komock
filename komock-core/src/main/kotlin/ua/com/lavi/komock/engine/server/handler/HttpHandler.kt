package ua.com.lavi.komock.engine.server.handler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Default http handler
 */

open class HttpHandler(routingTable: RoutingTable) : AbstractHttpHandler(routingTable) {

    override fun doHandle(
            target: String,
            jettyRequest: org.eclipse.jetty.server.Request,
            httpServletRequest: HttpServletRequest,
            httpServletResponse: HttpServletResponse) {

        val cachedHttpServletRequest = HttpRequestWrapper(httpServletRequest)
        val response = handle(cachedHttpServletRequest, httpServletResponse)

        serializeResponse(cachedHttpServletRequest, httpServletResponse, response)
    }
}