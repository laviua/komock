package ua.com.lavi.komock.http.handler.after

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.ext.VariableResolver
import ua.com.lavi.komock.model.Request
import ua.com.lavi.komock.model.Response
import ua.com.lavi.komock.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 10.07.17.
 */

class LogAfterResponseHandlerImpl(private val routeProperties: RouteProperties) : AfterResponseHandler {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun handle(request: Request, response: Response) {

        //log response data
        if (routeProperties.logResponse) {
            val logLine = VariableResolver.resolve(parametersMap(request, response), routeProperties.logResponseTemplate)
            log.info(logLine)
        }
    }

    private fun parametersMap(request: Request, response: Response): MutableMap<String, String> {
        val parametersMap: MutableMap<String, String> = HashMap()
        parametersMap.put("url", request.getHttpServletRequest().requestURL.toString())
        parametersMap.put("body", response.getContent())
        parametersMap.put("headers", response.getHeaders().toString())
        return parametersMap
    }
}