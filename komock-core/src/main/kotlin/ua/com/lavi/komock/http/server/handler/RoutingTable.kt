package ua.com.lavi.komock.http.server.handler

import ua.com.lavi.komock.http.handler.after.AfterResponseHandler
import ua.com.lavi.komock.http.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.http.handler.callback.CallbackHandler
import ua.com.lavi.komock.http.handler.response.ResponseHandler
import ua.com.lavi.komock.model.HttpMethod
import ua.com.lavi.komock.model.Route
import java.util.*

/**
 * Created by Oleksandr Loushkin
 * Class represents routing table. It should add route. find a route by direct url or by url mask.
 */

class RoutingTable {

    private val routeMap = HashMap<String, HashMap<HttpMethod, Route>>()
    private val REGEX_URL_WILDCARD = "([A-Za-z0-9_.-~]+)"

    fun addRoute(url: String,
                 httpMethod: HttpMethod,
                 responseHandler: ResponseHandler,
                 beforeResponseHandler: BeforeResponseHandler,
                 afterResponseHandler: AfterResponseHandler,
                 callbackHandler: CallbackHandler) {
        var urlMap: HashMap<HttpMethod, Route>? = routeMap[url]
        if (urlMap == null) {
            urlMap = HashMap()
        }
        if (find(httpMethod, url) != null) {
            throw RuntimeException("Route with httpMethod: ${httpMethod.name} and requestedUrl: $url is already exists in the routing table")
        }
        urlMap[httpMethod] = Route(url, responseHandler, beforeResponseHandler, afterResponseHandler, callbackHandler)
        routeMap[url] = urlMap
    }

    fun find(httpMethod: HttpMethod, requestedUrl: String): Route? {
        val httpMethodsMap = routeMap[requestedUrl]
        if (httpMethodsMap == null) {
            for (routeKey in routeMap.keys) {
                if (routeKey == "/**") {
                    return routeMap[routeKey]?.get(httpMethod)
                }
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
