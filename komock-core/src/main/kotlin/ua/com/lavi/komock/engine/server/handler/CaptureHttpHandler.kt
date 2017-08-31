package ua.com.lavi.komock.engine.server.handler

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.CaptureProperties
import ua.com.lavi.komock.engine.model.config.http.CapturedData
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Class decorator capture all requests and processed responses
 * Created by Oleksandr Loushkin on 20.08.17.
 */

class CaptureHttpHandler(private val captureProperties: CaptureProperties,
                         routingTable: RoutingTable) : AbstractHttpHandler(routingTable) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val capturedData: MutableList<CapturedData> = ArrayList()

    override fun doHandle(target: String,
                          jettyRequest: org.eclipse.jetty.server.Request,
                          httpServletRequest: HttpServletRequest,
                          httpServletResponse: HttpServletResponse) {

        val cachedHttpServletRequest = HttpRequestWrapper(httpServletRequest)
        val response = handle(cachedHttpServletRequest, httpServletResponse)

        capture(cachedHttpServletRequest, response)

        serializeResponse(cachedHttpServletRequest, httpServletResponse, response)
    }

    private fun capture(httpServletRequest: HttpServletRequest, response: Response) {
        if (capturedData.size < captureProperties.bufferSize) {
            capturedData.add(CapturedData(Request(httpServletRequest), response))
            if (log.isDebugEnabled) {
                log.debug("Captured data has been added to the buffer. Current size: ${captureProperties.bufferSize}")
            }
        } else {
            log.warn("Buffer size: ${captureProperties.bufferSize} is fully.")
        }
    }

    fun getCapturedData(): List<CapturedData> {
        return capturedData
    }
}

