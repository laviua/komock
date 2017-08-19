package ua.com.lavi.komock.engine.handler.before

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.VariableResolver
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class LogBeforeResponseHandlerImpl(private val routeProperties: RouteProperties) : BeforeResponseHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val counter: AtomicInteger = AtomicInteger(1)
    private val KEY_COUNTER = "counter"
    private val KEY_URL = "url"
    private val KEY_BODY = "body"
    private val KEY_HEADERS = "headers"

    override fun handle(request: Request, response: Response) {
        //log request data
        if (routeProperties.logRequest) {
            val logLine = VariableResolver.resolve(parametersMap(request), routeProperties.logRequestTemplate)
            log.info(logLine)
        }
    }

    private fun parametersMap(request: Request): MutableMap<String, String> {
        val parametersMap: MutableMap<String, String> = HashMap()
        parametersMap.put(KEY_COUNTER, counter.getAndIncrement().toString())
        parametersMap.put(KEY_URL, request.httpServletRequest().requestURL.toString())
        parametersMap.put(KEY_BODY, request.requestBody())
        parametersMap.put(KEY_HEADERS, request.getHeaders().toString())
        return parametersMap
    }

}