package ua.com.lavi.komock.engine.server.handler

import java.io.ByteArrayInputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Created by Oleksandr Loushkin on 20.08.17.
 */
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
