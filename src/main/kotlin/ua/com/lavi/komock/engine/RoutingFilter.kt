package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.zip.GZIPOutputStream
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Oleksandr Loushkin
 */

internal class RoutingFilter(val routingTable: RoutingTable) : Filter {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun init(config: FilterConfig) {
        //
    }

    @Throws(IOException::class, ServletException::class)
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
            log.info("The requested route $requestUri is not mapped")
            httpServletResponse.status = HttpServletResponse.SC_NOT_FOUND
        } else {
            val request: Request = Request(httpServletRequest)
            route.beforeRouteHandler.handle(request, response)
            route.routeHandler.handle(request, response)
            route.afterRouteHandler.handle(request, response)
        }
        serializeContentToResponse(httpServletRequest, httpServletResponse, response.content)

        chain?.doFilter(httpServletRequest, httpServletResponse)
    }

    fun serializeContentToResponse(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, content: String) {
        if (!httpServletResponse.isCommitted) {
            val responseStream = gzip(httpServletRequest, httpServletResponse)
            try {
                responseStream.write(content.toByteArray())
            } catch (e: UnsupportedEncodingException) {
                throw IOException(e)
            }
            responseStream.flush()
            responseStream.close()
        }
    }

    @Throws(IOException::class)
    fun gzip(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): OutputStream {
        var responseStream: OutputStream = httpResponse.outputStream

        val gzipAccepted = isGzipAccepted(httpRequest)
        val gzipSupported = httpResponse.getHeaders("Content-Encoding").contains("gzip")

        if (gzipAccepted && gzipSupported) {
            responseStream = GZIPOutputStream(responseStream, true)
        }
        return responseStream
    }

    private fun isGzipAccepted(httpServletRequest: HttpServletRequest): Boolean {
        val headers = httpServletRequest.getHeaders("Accept-Encoding")
        while (headers.hasMoreElements()) {
            if (headers.nextElement().contains("gzip")) return true
        }
        return false
    }

    override fun destroy() {}
}