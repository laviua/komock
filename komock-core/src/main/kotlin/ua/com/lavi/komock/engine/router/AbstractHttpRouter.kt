package ua.com.lavi.komock.engine.router

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.handler.after.AfterResponseHandler
import ua.com.lavi.komock.engine.handler.after.EmptyAfterResponseHandlerImpl
import ua.com.lavi.komock.engine.handler.after.LogAfterResponseHandlerImpl
import ua.com.lavi.komock.engine.handler.before.BeforeResponseHandler
import ua.com.lavi.komock.engine.handler.before.EmptyBeforeResponseHandlerImpl
import ua.com.lavi.komock.engine.handler.before.LogBeforeResponseHandlerImpl
import ua.com.lavi.komock.engine.handler.callback.CallbackHandler
import ua.com.lavi.komock.engine.handler.callback.CallbackHandlerImpl
import ua.com.lavi.komock.engine.handler.callback.EmptyCallbackHandlerImpl
import ua.com.lavi.komock.engine.handler.response.ResponseHandler
import ua.com.lavi.komock.engine.handler.response.RoutedResponseHandlerImpl
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import ua.com.lavi.komock.engine.server.JettyServer

/**
 * Created by Oleksandr Loushkin on 05.08.17.
 */
abstract class AbstractHttpRouter(val server: JettyServer) : HttpRouter {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var isStarted: Boolean = false

    override fun start() {
        if (!isStarted) {
            server.start()
            isStarted = true
        } else {
            log.info("Unable to start because server is already started!")
        }
    }

    override fun stop() {
        if (isStarted) {
            server.stop()
            isStarted = false
        } else {
            log.info("Unable to stop because server was not started!")
        }
    }

    /**
     * Add route by route properties configuration. It will create before, after, callback handlers
     */
    override fun addRoute(routeProperties: RouteProperties) {

        val beforeRouteHandler = if (routeProperties.logRequest) {
            LogBeforeResponseHandlerImpl(routeProperties)
        } else {
            EmptyBeforeResponseHandlerImpl()
        }
        val afterRouteHandler = if (routeProperties.logResponse) {
            LogAfterResponseHandlerImpl(routeProperties)
        } else {
            EmptyAfterResponseHandlerImpl()
        }

        val responseHandler = RoutedResponseHandlerImpl(routeProperties)
        val callbackHandler = CallbackHandlerImpl(routeProperties)

        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)
        val url = routeProperties.url

        addRoute(url, httpMethod, responseHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)
    }

    /**
     * Add route with empty before, after and callback handlers.
     */
    override fun addRoute(url: String,
                          httpMethod: HttpMethod,
                          responseHandler: ResponseHandler) {

        addRoute(url,
                httpMethod,
                responseHandler,
                EmptyBeforeResponseHandlerImpl(),
                EmptyAfterResponseHandlerImpl(),
                EmptyCallbackHandlerImpl())
    }

    override fun addRoute(url: String,
                          httpMethod: HttpMethod,
                          responseHandler: ResponseHandler,
                          beforeRouteHandler: BeforeResponseHandler,
                          afterRouteHandler: AfterResponseHandler,
                          callbackHandler: CallbackHandler) {

        server.routingTable().addRoute(url, httpMethod, responseHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)

        log.info("Registered http route: $httpMethod $url")
    }

    override fun deleteRoute(url: String, httpMethod: HttpMethod) {
        server.routingTable().deleteRoute(url, httpMethod)
        log.info("Removed route: $httpMethod $url")
    }

    override fun deleteRoute(routeProperties: RouteProperties) {
        val url = routeProperties.url
        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)
        deleteRoute(url, httpMethod)
    }

    override fun addVirtualHosts(virtualHosts: List<String>) {
        server.addVirtualHosts(virtualHosts)
        log.info("Added virtual hosts: $virtualHosts")
    }

    override fun deleteVirtualHosts(virtualHosts: List<String>) {
        server.removeVirtualHosts(virtualHosts)
        log.info("Removed virtual hosts: $virtualHosts")
    }
}