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
             val ipAddress: String,
             val port: Int,
             var minThreads: Int,
             var maxThreads: Int,
             var threadIdleTimeoutMillis: Int,
             var sslKeyStore: SslKeyStore?, virtualHosts: ArrayList<String>) {

    private var isStarted: Boolean = false

    private var isStarted: Boolean = false

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


    private val log = LoggerFactory.getLogger(this.javaClass)
    private var server: JettyServer
    private var routingTable = RoutingTable()


    init {
        server = JettyServer(serverId, virtualHosts, HttpHandler(RoutingFilter(routingTable)))
        routers.add(this)
    }

    @Synchronized fun stop() {
        if (isStarted) {
            Thread {
                routingTable.clearRoutes()
                server.stop()
            }.start()
            isStarted = false
        }
    }


    @Synchronized fun start() {
        if (!isStarted) {
            Thread {
                server.start(
                        ipAddress,
                        port,
                        sslKeyStore,
                        maxThreads,
                        minThreads,
                        threadIdleTimeoutMillis)
            }.start()
            isStarted = true
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

    fun deleteRoute(url: String, httpMethod: HttpMethod) {
        routingTable.deleteRoute(url, httpMethod)
    }

}
