package ua.com.lavi.komock.engine.handler

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class LogBeforeResponseHandler(val routeProperties: RouteProperties) : BeforeResponseHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val counter: AtomicInteger = AtomicInteger(1)

    override fun handle(request: Request, response: Response) {
        //log request data
        if (routeProperties.logRequest) {
            log.info(
                    "[${counter.getAndIncrement()}] - " +
                    "Url: ${request.httpServletRequest().requestURL}. " +
                    "Body: ${request.requestBody()}. " +
                    "Headers: ${request.getHeaders()}")
        }

        //show custom text in the log
        if (routeProperties.logBefore.isNotEmpty()) {
            log.info(routeProperties.logBefore)
        }
    }

}