package ua.com.lavi.komock.engine

import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.util.concurrent.TimeUnit


/**
 * Created by Oleksandr Loushkin
 */

internal class JettyServer(val serverProps: HttpServerProperties,
                           val httpHandler: Handler,
                           val sslKeyStore: SslKeyStore?) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var jettyServer: Server = Server(QueuedThreadPool(
            serverProps.maxThreads,
            serverProps.minThreads,
            serverProps.idleTimeout)
    )

    init {
        val contextHandler = ContextHandler(serverProps.contextPath)
        contextHandler.virtualHosts = serverProps.virtualHosts.toTypedArray()
        contextHandler.handler = httpHandler
        val handlerList: HandlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList
        jettyServer.connectors = arrayOf(buildServerConnector(serverProps.host, serverProps.port, sslKeyStore))
    }



    fun start() {
        jettyServer.start()
        log.debug("${serverProps.name} - listening on ${serverProps.host}:${serverProps.port}")
    }

    fun stop() {
        log.debug("Stopping ${serverProps.name}")
        jettyServer.stop()
        log.debug("${serverProps.name} is stopped")
    }

    /**
     * Add virtual hosts to the running server
     */
    fun addVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().addVirtualHosts(virtualHosts.toTypedArray())
    }

    /**
     * Remove virtual host from the running server
     */
    fun removeVirtualHosts(virtualHosts: List<String>) {
        getContextHandler().removeVirtualHosts(virtualHosts.toTypedArray())
    }

    private fun getContextHandler(): ContextHandler {
        return (jettyServer.handler as HandlerList).handlers[0] as ContextHandler
    }

    private fun buildServerConnector(
            host: String,
            port: Int,
            sslKeyStore: SslKeyStore?): ServerConnector {

        val httpConfig = HttpConfiguration()
        httpConfig.sendServerVersion = false // do not show jetty version
        val httpFactory = HttpConnectionFactory(httpConfig)

        val serverConnector: ServerConnector

        if (sslKeyStore == null) {
            serverConnector = ServerConnector(jettyServer, httpFactory)
        } else {
            val sslContextFactory = SslContextFactory()
            sslContextFactory.keyStoreResource = sslKeyStore.keystoreResource
            sslContextFactory.setKeyStorePassword(sslKeyStore.keystorePassword)
            serverConnector = ServerConnector(jettyServer, sslContextFactory, httpFactory)
        }
        serverConnector.idleTimeout = TimeUnit.HOURS.toMillis(1) // 3600000
        serverConnector.soLingerTime = -1 // linger time disabled
        serverConnector.host = host
        serverConnector.port = port
        return serverConnector
    }
}



