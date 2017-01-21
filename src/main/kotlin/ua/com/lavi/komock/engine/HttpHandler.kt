package ua.com.lavi.komock.engine

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.session.SessionHandler
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import javax.servlet.Filter
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Created by Oleksandr Loushkin
 */

internal class HttpHandler(private val requestMatcherFilter: Filter) : SessionHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun doHandle(
            target: String,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse) {

        requestMatcherFilter.doFilter(HttpRequestWrapper(request), response, null)
    }

    class HttpRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
        private var cachedBytes: ByteArray? = null

        @Throws(IOException::class)
        override fun getInputStream(): ServletInputStream {
            if (cachedBytes == null) {
                cachedBytes = toByteArray(super.getInputStream())
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
        @Throws(IOException::class)
        fun toByteArray(input: InputStream): ByteArray {
            val os = ByteArrayOutputStream()
            val buf = ByteArray(1024)
            var n = input.read(buf)
            while (n != -1) {
                os.write(buf, 0, n)
                n = input.read(buf)
            }
            return os.toByteArray()
        }
    }
}

