package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.RouteProperties

/**
 * Created by Oleksandr Loushkin
 * This class represents all logic according to manage server and link route with the server
 */

class Router(val serverName: String,
             val host: String,
             val port: Int,
             var minThreads: Int,
             var maxThreads: Int,
             var idleTimeout: Int,
             var sslKeyStore: SslKeyStore?,
             var virtualHosts: MutableList<String>) {

    private var isStarted: Boolean = false
    private var server: JettyServer
    private var routingTable = RoutingTable()

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        val httpHandler = HttpHandler(RoutingFilter(routingTable))
        server = JettyServer(serverName,
                virtualHosts,
                httpHandler,
                host,
                port,
                sslKeyStore,
                maxThreads,
                minThreads,
                idleTimeout)
    }

    fun start() {
        if (!isStarted) {
            server.start()
            isStarted = true
            log.info("Started server: $serverName on port: $port, virtualHosts: $virtualHosts. " +
                    "maxThreads: $maxThreads, minThreads: $minThreads, idle timeout: $idleTimeout ms")
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

        val routeHandlerBuilder = RequestHandlerBuilder(routeProperties)

        val beforeRouteHandler = routeHandlerBuilder.beforeRouteHandler()
        val afterRouteHandler = routeHandlerBuilder.afterRequestHandler()
        val routeHandler = routeHandlerBuilder.routeHandler()

        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)

        routingTable.addRoute(routeProperties.url, httpMethod, routeHandler, beforeRouteHandler, afterRouteHandler)

        log.info("Registered http route: ${routeProperties.httpMethod} ${routeProperties.url}")
    }

    fun addVirtualHosts(virtualHosts: List<String>) {
        this.virtualHosts.addAll(virtualHosts)
        server.addVirtualHosts(virtualHosts)
        log.debug("Added virtual hosts: $virtualHosts")
    }

    fun removeVirtualHosts(virtualHosts: List<String>) {
        this.virtualHosts.removeAll(virtualHosts)
        server.removeVirtualHosts(virtualHosts)
        log.debug("Removed virtual hosts: $virtualHosts")
    }

    fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routingTable.deleteRoute(url, httpMethod)
        log.debug("Removed route: $url")
    }

}