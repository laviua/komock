package ua.com.lavi.komock.engine

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.config.property.http.RouteProperties
import ua.com.lavi.komock.engine.handler.AfterRouteHandler
import ua.com.lavi.komock.engine.handler.BeforeRouteHandler
import ua.com.lavi.komock.engine.handler.RouteHandler
import ua.com.lavi.komock.engine.model.HttpMethod
import ua.com.lavi.komock.engine.model.Request
import ua.com.lavi.komock.engine.model.Response
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class Router(val serverId: String,
             val host: String,
             val port: Int,
             var minThreads: Int,
             var maxThreads: Int,
             var idleTimeout: Int,
             var sslKeyStore: SslKeyStore?,
             var virtualHosts: ArrayList<String>) {

    private var isStarted: Boolean = false
    private val log = LoggerFactory.getLogger(this.javaClass)
    private var server: JettyServer
    private var routingTable = RoutingTable()

    companion object {

        private val routers = ArrayList<Router>()

        @JvmStatic
        fun startAllRouters() {
            routers.forEach(Router::start)
        }

        @JvmStatic
        fun stopAllRouters() {
            routers.forEach(Router::stop)
        }
    }

    init {
        val httpHandler = HttpHandler(RoutingFilter(routingTable))
        server = JettyServer(serverId, virtualHosts, httpHandler, host, port, sslKeyStore, maxThreads, minThreads, idleTimeout)
        routers.add(this)
    }

    fun start() {
        if (!isStarted) {
            server.start()
            isStarted = true
            log.info("Started server: $serverId on port: $port, virtualHosts: ${virtualHosts.joinToString(",")}. " +
                    "maxThreads: $maxThreads, minThreads: $minThreads, idle timeout: $idleTimeout ms")
        } else {
            log.info("Server is already started!")
        }
    }

    fun stop() {
        if (isStarted) {
            server.stop()
            isStarted = false
        } else {
            log.info("Server is not started!")
        }
    }


    fun addRoute(routeProperties: RouteProperties) {

        val beforeRouteHandler = object : BeforeRouteHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logRequest) {
                    log.info("url: ${routeProperties.url}. RequestBody: ${request.requestBody()}")
                }
                if (routeProperties.logBefore.isNotEmpty()) {
                    log.info(routeProperties.logBefore)
                }
            }
        }


        val afterRouteHandler = object : AfterRouteHandler {
            override fun handle(request: Request, response: Response) {
                if (routeProperties.logResponse) {
                    log.info("url: ${routeProperties.url}. ResponseBody: ${response.content}")
                }

                if (routeProperties.logAfter.isNotEmpty()) {
                    log.info(routeProperties.logAfter)
                }
            }
        }

        val routeHandler = object : RouteHandler {
            override fun handle(request: Request, response: Response) {
                response.contentType(routeProperties.contentType)
                response.statusCode(routeProperties.code)
                response.content = routeProperties.responseBody

                routeProperties.responseHeaders.forEach {
                    it.forEach {
                        response.addHeader(it.key, it.value)
                    }
                }

                routeProperties.cookies.forEach {
                    response.addCookie(it)
                }
            }
        }

        val httpMethod = HttpMethod.retrieveMethod(routeProperties.httpMethod)

        routingTable.addRoute(routeProperties.url, httpMethod, routeHandler, beforeRouteHandler, afterRouteHandler)

        log.info("Registered http route: ${routeProperties.httpMethod} ${routeProperties.url}")
    }

    fun addVirtualHosts(virtualHosts: ArrayList<String>) {
        server.addVirtualHosts(virtualHosts)
    }

    fun removeVirtualHosts(virtualHosts: ArrayList<String>) {
        server.removeVirtualHosts(virtualHosts)
    }

    fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routingTable.deleteRoute(url, httpMethod)
    }

}