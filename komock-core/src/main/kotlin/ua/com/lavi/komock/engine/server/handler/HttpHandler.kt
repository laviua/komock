package ua.com.lavi.komock.engine.server.handler

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.session.SessionHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Oleksandr Loushkin
 * This is an entry point of thw application
 * Serialize route properties content to the http response
 */

open class HttpHandler(routingTable: RoutingTable) : SessionHandler() {

    private val routingFilter: RoutingFilter = RoutingFilter(routingTable)

    override fun doHandle(
            target: String,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse) {

        routingFilter.doFilter(HttpRequestWrapper(request), response, null)
    }
}