package ua.com.lavi.komock.engine

import ua.com.lavi.komock.engine.handler.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.ResponseHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Route
import java.util.*

/**
 * Created by Oleksandr Loushkin
 * Class represents routing table. It should add route. find a route by direct url or by url mask.
 */

internal class RoutingTable {

    private val routeMap = HashMap<String, HashMap<HttpMethod, Route>>()
    private val REGEX_URL_WILDCARD = "([A-Za-z0-9_.-~]+)"

    fun addRoute(url: String,
                 httpMethod: HttpMethod,
                 responseHandler: ResponseHandler,
                 beforeResponseHandlers: List<BeforeResponseHandler>,
                 afterResponseHandlers: List<AfterResponseHandler>,
                 callbackHandler: CallbackHandler) {
        var urlMap: HashMap<HttpMethod, Route>? = routeMap[url]
        if (urlMap == null) {
            urlMap = HashMap<HttpMethod, Route>()
        }
        if (find(httpMethod, url) != null) {
            throw RuntimeException("Route with httpMethod: '" + httpMethod.name + "' and requestedUrl: '" + url + "' is already exists in the routing table")
        }
        urlMap.put(httpMethod, Route(url, httpMethod, responseHandler, beforeResponseHandlers, afterResponseHandlers, callbackHandler))
        routeMap.put(url, urlMap)
    }

    fun find(httpMethod: HttpMethod, requestedUrl: String): Route? {
        val httpMethodsMap = routeMap[requestedUrl]
        if (httpMethodsMap == null) {
            for (routeKey in routeMap.keys) {
                val pattern = routeKey.replace("*", REGEX_URL_WILDCARD)
                if (Regex(pattern).matchEntire(requestedUrl) != null) {
                    return routeMap[routeKey]!![httpMethod]
                }
            }
            return null
        } else {
            return httpMethodsMap[httpMethod]
        }
    }

    fun clearRoutes() {
        routeMap.clear()
    }

    fun getFullRouteMap(): Map<String, MutableMap<HttpMethod, Route>> {
        return routeMap
    }

    fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routeMap[url]?.remove(httpMethod)
        if (routeMap[url]?.size == 0) {
            routeMap.remove(url)
        }
    }
}
