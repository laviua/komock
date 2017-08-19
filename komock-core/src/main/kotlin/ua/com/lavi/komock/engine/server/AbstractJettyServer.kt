package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.router.RoutingTable
import java.util.concurrent.TimeUnit

/**
 * Created by Oleksandr Loushkin
 */

abstract class AbstractJettyServer(val serverProps: HttpServerProperties,
                                   val httpHandler: HttpHandler) : JettyServer {

    private val log = LoggerFactory.getLogger(this.javaClass)

    var jettyServer: Server = Server(NamedQueuedThreadPool(
            serverProps.maxThreads,
            serverProps.minThreads,
            serverProps.idleTimeout,
            serverProps.name)
    )

    init {
        val contextHandler = ContextHandler(serverProps.contextPath)
        contextHandler.virtualHosts = serverProps.virtualHosts.toTypedArray()
        contextHandler.handler = httpHandler
        val handlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList

        val serverConnector = buildServerConnector()
        serverConnector.idleTimeout = TimeUnit.HOURS.toMillis(1) // 3600000
        serverConnector.soLingerTime = -1 // linger time disabled
        serverConnector.host = serverProps.host
        serverConnector.port = serverProps.port
        jettyServer.connectors = arrayOf(serverConnector)
    }

    override fun start() {
        jettyServer.start()
        log.info("Started server: ${serverProps.name} on port: ${serverProps.port}. " +
                "VirtualHosts: ${serverProps.virtualHosts}. " +
                "MaxThreads: ${serverProps.maxThreads}. " +
                "MinThreads: ${serverProps.minThreads}. " +
                "Idle timeout: ${serverProps.idleTimeout} ms")
        MDC.put("serverName", serverProps.name)
    }

    override fun stop() {
        log.info("Stopping server: ${serverProps.name}")
        jettyServer.stop()
        log.info("Server: ${serverProps.name} is stopped")
    }

    /**
     * Add virtual hosts to the running server
     */
    override fun addVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().addVirtualHosts(virtualHosts.toTypedArray())
    }

    /**
     * Remove virtual host from the running server
     */
    override fun removeVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().removeVirtualHosts(virtualHosts.toTypedArray())
    }

    private fun getContextHandler(): ContextHandler {
        return (jettyServer.handler as HandlerList).handlers[0] as ContextHandler
    }

    abstract fun buildServerConnector(): ServerConnector

    open fun httpConfig(): HttpConfiguration {
        val httpConfig = HttpConfiguration()
        httpConfig.sendServerVersion = false // do not show jetty version
        return httpConfig
    }

    override fun routingTable(): RoutingTable {
        return httpHandler.routingTable
    }
}

