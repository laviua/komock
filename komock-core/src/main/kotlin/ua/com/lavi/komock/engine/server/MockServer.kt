package ua.com.lavi.komock.engine.server

import ua.com.lavi.komock.engine.handler.after.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.callback.CallbackHandler
import ua.com.lavi.komock.engine.handler.response.ResponseHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.config.http.CapturedData
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */
interface MockServer {
    /**
     * Start http server
     */
    fun start()

    /**
     * Stop http server
     */
    fun stop()

    /**
     * Add virtual host in runtime
     */
    fun addVirtualHosts(virtualHosts: List<String>)

    /**
     * Delete virtual host in runtime
     */
    fun deleteVirtualHosts(virtualHosts: List<String>)

    /**
     * Add route in runtime
     */
    fun addRoute(routeProperties: RouteProperties)

    /**
     * Add route in runtime
     */
    fun addRoute(url: String, httpMethod: HttpMethod, responseHandler: ResponseHandler)

    /**
     * Add route in runtime
     */
    fun addRoute(url: String, httpMethod: HttpMethod, responseHandler: ResponseHandler, beforeRouteHandler: BeforeResponseHandler, afterRouteHandler: AfterResponseHandler, callbackHandler: CallbackHandler)

    /**
     * Delete route in runtime
     */
    fun deleteRoute(url: String, httpMethod: HttpMethod)

    /**
     * Delete route in runtime
     */
    fun deleteRoute(routeProperties: RouteProperties)

    /**
     * Get captured data. If capture option has enable state. It is not in runtime switch option
     */
    fun getCapturedData(): List<CapturedData>

    /**
     * Get configured server's name
     */
    fun getName() : String
}