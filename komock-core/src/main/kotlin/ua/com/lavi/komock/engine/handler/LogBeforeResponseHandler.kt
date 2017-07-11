package ua.com.lavi.komock.engine.handler

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.VariableResolver
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
    private val variableResolver: VariableResolver = VariableResolver()

    override fun handle(request: Request, response: Response) {
        //log request data
        if (routeProperties.logRequest) {
            val logLine = variableResolver.resolve(parametersMap(request), routeProperties.logRequestTemplate)
            log.info(logLine)
        }

        //show custom text in the log
        if (routeProperties.logBefore.isNotEmpty()) {
            log.info(routeProperties.logBefore)
        }
    }

    fun parametersMap(request: Request): MutableMap<String, String> {
        val parametersMap: MutableMap<String,String> = HashMap()
        parametersMap.put("counter", counter.getAndIncrement().toString())
        parametersMap.put("url", request.httpServletRequest().requestURL.toString())
        parametersMap.put("body", request.requestBody())
        parametersMap.put("headers", request.getHeaders().toString())
        return parametersMap
    }

}