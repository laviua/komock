package ua.com.lavi.komock.engine.handler

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.VariableResolver
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */
class LogAfterResponseHandler(val routeProperties: RouteProperties) : AfterResponseHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val variableResolver: VariableResolver = VariableResolver()

    override fun handle(request: Request, response: Response) {

        //log response data
        if (routeProperties.logResponse) {
            val logLine = variableResolver.resolve(parametersMap(request, response), routeProperties.logResponseTemplate)
            log.info(logLine)
        }

        //show custom text in the log
        if (routeProperties.logAfter.isNotEmpty()) {
            log.info(routeProperties.logAfter)
        }
    }

    fun parametersMap(request: Request, response: Response): MutableMap<String, String> {
        val parametersMap: MutableMap<String,String> = HashMap()
        parametersMap.put("url", request.httpServletRequest().requestURL.toString())
        parametersMap.put("body", response.content)
        parametersMap.put("headers", response.getHeaders().toString())
        return parametersMap
    }
}