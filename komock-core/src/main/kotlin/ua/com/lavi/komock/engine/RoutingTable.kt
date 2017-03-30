package ua.com.lavi.komock.engine

import ua.com.lavi.komock.engine.handler.AfterRequestHandler
import ua.com.lavi.komock.engine.handler.BeforeRequestHandler
import ua.com.lavi.komock.engine.handler.CallbackHandler
import ua.com.lavi.komock.engine.handler.RequestHandler
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
                 requestHandler: RequestHandler,
                 beforeRequestHandler: BeforeRequestHandler,
                 afterRequestHandler: AfterRequestHandler,
                 callbackHandler: CallbackHandler) {
        var urlMap: HashMap<HttpMethod, Route>? = routeMap[url]
        if (urlMap == null) {
            urlMap = HashMap<HttpMethod, Route>()
        }
        urlMap.put(httpMethod, Route(url, httpMethod, requestHandler, beforeRequestHandler, afterRequestHandler, callbackHandler))
        routeMap.put(url, urlMap)
    }

    fun find(httpMethod: HttpMethod, url: String): Route? {
        val httpMethodsMap = routeMap[url]
        if (httpMethodsMap == null) {
            for (routeKey in routeMap.keys) {
                val pattern = routeKey.replace("*", REGEX_URL_WILDCARD)
                if (Regex(pattern).matchEntire(url) != null) {
                    val route = routeMap[routeKey]!![httpMethod]
                    if (route == null) {
                        return null
                    } else {
                        return route
                    }
                }
            }
            return null
        } else {
            val route = httpMethodsMap[httpMethod]
            if (route == null) {
                return null
            } else {
                return route
            }
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
