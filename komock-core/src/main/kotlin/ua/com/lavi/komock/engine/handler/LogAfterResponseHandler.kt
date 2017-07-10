package ua.com.lavi.komock.engine.handler

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class LogAfterResponseHandler(val routeProperties: RouteProperties) : AfterResponseHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(request: Request, response: Response) {

        //log response data
        if (routeProperties.logResponse) {
            log.info(
                    "Url: ${request.httpServletRequest().requestURL}. " +
                    "Body: ${response.content}. " +
                    "Headers: ${response.getHeaders()}")
        }

        //show custom text in the log
        if (routeProperties.logAfter.isNotEmpty()) {
            log.info(routeProperties.logAfter)
        }
    }
}