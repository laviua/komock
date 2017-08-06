package ua.com.lavi.komock.engine.router

import ua.com.lavi.komock.engine.handler.after.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.callback.CallbackHandler
import ua.com.lavi.komock.engine.handler.response.ResponseHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 05.08.17.
 */
interface HttpRouter {
    fun start()
    fun stop()
    fun addRoute(routeProperties: RouteProperties)
    fun addRoute(url: String,
                 httpMethod: HttpMethod,
                 responseHandler: ResponseHandler)

    fun addRoute(url: String,
                 httpMethod: HttpMethod,
                 responseHandler: ResponseHandler,
                 beforeRouteHandler: BeforeResponseHandler,
                 afterRouteHandler: AfterResponseHandler,
                 callbackHandler: CallbackHandler)

    fun deleteRoute(url: String, httpMethod: HttpMethod)
    fun deleteRoute(routeProperties: RouteProperties)
    fun addVirtualHosts(virtualHosts: List<String>)
    fun deleteVirtualHosts(virtualHosts: List<String>)
}