package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.LoggerFactory
import org.slf4j.MDC
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
import ua.com.lavi.komock.engine.model.config.http.CapturedData
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.handler.CaptureHttpHandler
import ua.com.lavi.komock.engine.model.config.http.RouteProperties
import ua.com.lavi.komock.engine.server.handler.HttpHandler
import ua.com.lavi.komock.engine.server.handler.RoutingTable
import java.util.concurrent.TimeUnit

/**
 * Created by Oleksandr Loushkin
 */

abstract class AbstractMockServer(val serverProps: HttpServerProperties) : MockServer {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var isStarted: Boolean = false
    private val routingTable: RoutingTable = RoutingTable()

    var jettyServer: Server = Server(NamedQueuedThreadPool(
            serverProps.maxThreads,
            serverProps.minThreads,
            serverProps.idleTimeout,
            serverProps.name)
    )

    init {
        val contextHandler = ContextHandler(serverProps.contextPath)
        contextHandler.virtualHosts = serverProps.virtualHosts.toTypedArray()
        if (serverProps.capture.enabled) {
            contextHandler.handler = CaptureHttpHandler(serverProps.capture, routingTable)
        } else {
            contextHandler.handler = HttpHandler(routingTable)
        }
        val handlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList

        val serverConnector = buildServerConnector()
        serverConnector.idleTimeout = TimeUnit.HOURS.toMillis(1) // 3600000
        serverConnector.soLingerTime = -1 // linger time disabled
        serverConnector.host = serverProps.host
        serverConnector.port = serverProps.port
        jettyServer.connectors = arrayOf(serverConnector)

        //register only enabled routes
        serverProps.routes.filter { it.enabled }.forEach { addRoute(it) }
    }

    override fun start() {
        if (!isStarted) {
            jettyServer.start()
            log.info("Started server: ${serverProps.name} on port: ${serverProps.port}. " +
                    "VirtualHosts: ${serverProps.virtualHosts}. " +
                    "MaxThreads: ${serverProps.maxThreads}. " +
                    "MinThreads: ${serverProps.minThreads}. " +
                    "Idle timeout: ${serverProps.idleTimeout} ms")
            MDC.put("serverName", serverProps.name)
            isStarted = true
        } else {
            log.info("Unable to start because server is already started!")
        }
    }

    override fun stop() {
        if (isStarted) {
            log.info("Stopping server: ${serverProps.name}")
            jettyServer.stop()
            log.info("Server: ${serverProps.name} is stopped")
            isStarted = false
        } else {
            log.info("Unable to stop because server was not started!")
        }
    }

    override fun addVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().addVirtualHosts(virtualHosts.toTypedArray())
        log.info("Added virtual hosts: $virtualHosts")
    }

    override fun deleteVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().removeVirtualHosts(virtualHosts.toTypedArray())
        log.info("Removed virtual hosts: $virtualHosts")
    }

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
        val callbackHandler = CallbackHandlerImpl(routeProperties.callback)

        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)
        val url = routeProperties.url

        addRoute(url, httpMethod, responseHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)
    }

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
                          callbackHandler: CallbackHandler) {

        addRoute(url,
                httpMethod,
                responseHandler,
                EmptyBeforeResponseHandlerImpl(),
                EmptyAfterResponseHandlerImpl(),
                callbackHandler)
    }

    override fun addRoute(url: String,
                          httpMethod: HttpMethod,
                          responseHandler: ResponseHandler,
                          beforeRouteHandler: BeforeResponseHandler,
                          afterRouteHandler: AfterResponseHandler,
                          callbackHandler: CallbackHandler) {

        routingTable.addRoute(url, httpMethod, responseHandler, beforeRouteHandler, afterRouteHandler, callbackHandler)

        log.info("Registered http route: $httpMethod $url")
    }

    override fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routingTable.deleteRoute(url, httpMethod)
        log.info("Removed route: $httpMethod $url")
    }

    override fun deleteRoute(routeProperties: RouteProperties) {
        val url = routeProperties.url
        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)
        deleteRoute(url, httpMethod)
    }

    abstract fun buildServerConnector(): ServerConnector

    override fun getCapturedData(): List<CapturedData> {
        return if (serverProps.capture.enabled) {
            val captureHttpHandler = getContextHandler().handler as CaptureHttpHandler
            return captureHttpHandler.getCapturedData()
        } else {
            emptyList()
        }
    }

    override fun getName(): String {
        return serverProps.name
    }

    private fun getContextHandler(): ContextHandler {
        return (jettyServer.handler as HandlerList).handlers[0] as ContextHandler
    }

    open fun httpConfig(): HttpConfiguration {
        val httpConfig = HttpConfiguration()
        httpConfig.sendServerVersion = false // do not show jetty version
        return httpConfig
    }

    private inner class NamedQueuedThreadPool(maxThreads: Int, minThreads: Int, idleTimeout: Int, threadName: String) :
            QueuedThreadPool(maxThreads, minThreads, idleTimeout) {

        init {
            name = threadName
        }
    }
}