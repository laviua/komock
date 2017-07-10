package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import ua.com.lavi.komock.engine.handler.ResponseHandlerBuilder
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin
 * This class represents all logic according to manage server and link route with the server
 */

class Router(val serverProps: HttpServerProperties,
             val sslKeyStore: SslKeyStore?) {

    private var isStarted: Boolean = false
    private var server: JettyServer
    private var routingTable = RoutingTable()

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        server = JettyServer(serverProps, HttpHandler(RoutingFilter(routingTable)), sslKeyStore)
    }

    fun start() {
        if (!isStarted) {
            server.start()
            isStarted = true
            log.info("Started server: ${serverProps.name} on port: ${serverProps.port}. " +
                    "VirtualHosts: ${serverProps.virtualHosts}. " +
                    "MaxThreads: ${serverProps.maxThreads}. " +
                    "MinThreads: ${serverProps.minThreads}. " +
                    "Idle timeout: ${serverProps.idleTimeout} ms")
            MDC.put("serverName", serverProps.name)
        } else {
            log.info("Unable to start because server is already started!")
        }
    }

    fun stop() {
        if (isStarted) {
            server.stop()
            isStarted = false
            log.info("Server is stopped")
        } else {
            log.info("Unable to stop because server was not started!")
        }
    }


    fun addRoute(routeProperties: RouteProperties) {

        val handlerBuilder = ResponseHandlerBuilder(routeProperties)

        val beforeRouteHandlers = handlerBuilder.beforeRouteHandlers()
        val afterRouteHandlers = handlerBuilder.afterRequestHandlers()
        val routeHandler = handlerBuilder.routeHandler()
        val callbackHandler = handlerBuilder.callbackHandler()

        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)

        routingTable.addRoute(routeProperties.url,
                httpMethod,
                routeHandler,
                beforeRouteHandlers,
                afterRouteHandlers,
                callbackHandler)

        log.info("Registered http route: ${routeProperties.httpMethod} ${routeProperties.url}")
    }

    fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routingTable.deleteRoute(url, httpMethod)
        log.info("Removed route: $httpMethod $url")
    }

    fun deleteRoute(routeProperties:RouteProperties) {
        val url = routeProperties.url
        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)
        deleteRoute(url, httpMethod)
    }

    fun addVirtualHosts(virtualHosts: List<String>) {
        server.addVirtualHosts(virtualHosts)
        log.info("Added virtual hosts: $virtualHosts")
    }

    fun removeVirtualHosts(virtualHosts: List<String>) {
        server.removeVirtualHosts(virtualHosts)
        log.info("Removed virtual hosts: $virtualHosts")
    }

}