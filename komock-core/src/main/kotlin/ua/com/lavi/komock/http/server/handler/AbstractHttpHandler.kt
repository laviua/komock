package ua.com.lavi.komock.http.server.handler

import org.eclipse.jetty.server.session.SessionHandler
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.model.HttpMethod
import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response
import java.io.OutputStream
import java.util.*
import java.util.zip.GZIPOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Oleksandr Loushkin
 * This is an entry point of the request
 * Serialize route properties content to the http response
 */

abstract class AbstractHttpHandler(private val routingTable: RoutingTable) : SessionHandler() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    abstract override fun doHandle(
            target: String,
            jettyRequest: org.eclipse.jetty.server.Request,
            httpServletRequest: HttpServletRequest,
            httpServletResponse: HttpServletResponse)

    open fun handle(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse): Response {
        val requestUri = httpServletRequest.requestURI
        val response = Response(httpServletResponse)
        val httpMethod = HttpMethod.retrieveMethod(httpServletRequest.method)

        val route = routingTable.find(httpMethod, requestUri)
        if (route == null) {
            log.info("Requested route $requestUri is not mapped")
            httpServletResponse.status = HttpServletResponse.SC_NOT_FOUND
            return response
        } else {
            val request = Request(httpServletRequest)
            route.beforeResponseHandler.handle(request, response)
            route.responseHandler.handle(request, response)
            route.afterResponseHandler.handle(request, response)
            route.callbackHandler.handle(request, response)
        }
        return response
    }

    open fun serializeResponse(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, response: Response) {
        if (!httpServletResponse.isCommitted) {
            val responseStream = gzip(httpServletRequest, httpServletResponse)
            responseStream.write(response.getContent().toByteArray())
            responseStream.flush()
            responseStream.close()
        }
    }

    private fun gzip(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): OutputStream {
        var responseStream: OutputStream = httpResponse.outputStream

        if (isGzipAccepted(httpRequest) && httpResponse.getHeaders("Content-Encoding").contains("gzip")) {
            responseStream = GZIPOutputStream(responseStream, true)
        }
        return responseStream
    }

    private fun isGzipAccepted(httpServletRequest: HttpServletRequest): Boolean {
        val headers: Enumeration<String> = httpServletRequest.getHeaders("Accept-Encoding")
        while (headers.hasMoreElements()) {
            if (headers.nextElement().contains("gzip")) {
                return true
            }
        }
        return false
    }
}