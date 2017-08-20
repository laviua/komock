package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.session.SessionHandler
import java.io.ByteArrayInputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse

/**
 * Created by Oleksandr Loushkin
 */

class HttpHandler : SessionHandler() {

    //One routing table per one http server
    val routingTable: RoutingTable = RoutingTable()
    private val routingFilter: RoutingFilter = RoutingFilter(routingTable)

    override fun doHandle(
            target: String,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse) {

        routingFilter.doFilter(HttpRequestWrapper(request), response, null)
    }

    class HttpRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
        private var cachedBytes: ByteArray? = null

        override fun getInputStream(): ServletInputStream {
            if (cachedBytes == null) {
                cachedBytes = super.getInputStream().readBytes()
            }
            return CachedServletInputStream()
        }

        private inner class CachedServletInputStream : ServletInputStream() {
            private val byteArrayInputStream: ByteArrayInputStream = ByteArrayInputStream(cachedBytes!!)

            override fun read(): Int {
                return byteArrayInputStream.read()
            }

            override fun available(): Int {
                return byteArrayInputStream.available()
            }

            override fun isFinished(): Boolean {
                return available() <= 0
            }

            override fun isReady(): Boolean {
                return available() >= 0
            }

            override fun setReadListener(readListener: ReadListener) {}
        }
    }
}

