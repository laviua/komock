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
     * Start http server.
     */
    fun start()

    /**
     * Stop http server.
     */
    fun stop()

    /**
     * Add virtual hosts.
     * @param virtualHosts - List of the virtual hosts (domains)
     */
    fun addVirtualHosts(virtualHosts: List<String>)

    /**
     * Delete virtual hosts.
     * @param virtualHosts - List of the virtual hosts (domains)
     */
    fun deleteVirtualHosts(virtualHosts: List<String>)

    /**
     * Add route by route properties configuration. It will create before, after, callback handlers according to properties.
     * @param routeProperties - Route properties
     */
    fun addRoute(routeProperties: RouteProperties)

    /**
     * Add route with empty before, after and callback handlers.
     * @param url - URL
     * @param httpMethod - Http Method
     * @param responseHandler - Response handler
     */
    fun addRoute(url: String, httpMethod: HttpMethod, responseHandler: ResponseHandler)

    /**
     * Add route with empty before and after handlers.
     * @param url - URL
     * @param httpMethod - Http Method
     * @param responseHandler - Response handler
     * @param callbackHandler - Callback handler
     */
    fun addRoute(url: String, httpMethod: HttpMethod, responseHandler: ResponseHandler, callbackHandler: CallbackHandler)

    /**
     * Add route with custom handlers.
     * @param url - URL
     * @param httpMethod - Http Method
     * @param responseHandler - Response handler
     * @param beforeRouteHandler - Before response handler
     * @param afterRouteHandler - After response handler
     * @param callbackHandler - Callback handler
     */
    fun addRoute(url: String, httpMethod: HttpMethod, responseHandler: ResponseHandler, beforeRouteHandler: BeforeResponseHandler, afterRouteHandler: AfterResponseHandler, callbackHandler: CallbackHandler)

    /**
     * Delete existed route.
     * @param url - URL
     * @param httpMethod - Http Method
     */
    fun deleteRoute(url: String, httpMethod: HttpMethod)

    /**
     * Delete virtual host from the running server
     * @param routeProperties - Route properties
     */
    fun deleteRoute(routeProperties: RouteProperties)

    /**
     * Get captured data. If capture option has enable state. It is not in runtime switch option
     * @return List of the captured data
     */
    fun getCapturedData(): List<CapturedData>

    /**
     * Get configured mock server's name
     * @return Name of the mocked server
     */
    fun getName() : String
}