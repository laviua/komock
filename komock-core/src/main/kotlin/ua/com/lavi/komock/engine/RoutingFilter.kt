package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import java.io.OutputStream
import java.util.*
import java.util.zip.GZIPOutputStream
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This is an entry point of the mock service for http request
 * Serialize route properties content to the http response
 * Should be ThreadSafe
 * Created by Oleksandr Loushkin
 */
internal class RoutingFilter(val routingTable: RoutingTable) : Filter {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun init(config: FilterConfig) {
        //
    }

    override fun doFilter(servletRequest: ServletRequest,
                          servletResponse: ServletResponse,
                          chain: FilterChain?) {
        val httpServletRequest = servletRequest as HttpServletRequest
        val httpServletResponse = servletResponse as HttpServletResponse
        val requestUri = httpServletRequest.requestURI
        val response = Response(httpServletResponse)
        val httpMethod = HttpMethod.retrieveMethod(httpServletRequest.method)

        val route = routingTable.find(httpMethod, requestUri)
        if (route == null) {
            log.info("Requested route $requestUri is not mapped")
            httpServletResponse.status = HttpServletResponse.SC_NOT_FOUND
        } else {
            val request: Request = Request(httpServletRequest)
            route.beforeRequestHandler.handle(request, response)
            route.requestHandler.handle(request, response)
            route.afterRequestHandler.handle(request, response)
            route.callbackHandler.handle(request, response)
        }
        serializeContentToResponse(httpServletRequest, httpServletResponse, response.content)

        chain?.doFilter(httpServletRequest, httpServletResponse)
    }

    fun serializeContentToResponse(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, content: String) {
        if (!httpServletResponse.isCommitted) {
            val responseStream = gzip(httpServletRequest, httpServletResponse)
            responseStream.write(content.toByteArray())
            responseStream.flush()
            responseStream.close()
        }
    }

    fun gzip(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): OutputStream {
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

    override fun destroy() {}
}