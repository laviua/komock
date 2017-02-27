package ua.com.lavi.komock.engine


import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.util.concurrent.TimeUnit


/**
 * Created by Oleksandr Loushkin
 */

internal class JettyServer(val serverName: String,
                           val virtualHosts: List<String>,
                           val httpHandler: Handler,
                           val host: String,
                           val port: Int,
                           val sslKeyStore: SslKeyStore?,
                           maxThreads: Int,
                           minThreads: Int,
                           idleTimeout: Int) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private var jettyServer: Server

    init {
        jettyServer = Server(QueuedThreadPool(maxThreads, minThreads, idleTimeout))
        val contextHandler = buildContextHandler()
        val handlerList: HandlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList
    }

    fun start() {
        log.debug("$serverName - initializing on $host:$port")
        val serverConnector: ServerConnector = buildServerConnector(jettyServer, host, port, sslKeyStore)
        jettyServer = serverConnector.server
        jettyServer.connectors = arrayOf(serverConnector)
        jettyServer.start()
        log.debug("$serverName - listening on $host:$port")
    }

    fun stop() {
        log.debug("Stopping $serverName")
        jettyServer.stop()
        log.debug("$serverName is stopped")
    }

    /**
     * Add virtual hosts to the running server
     */
    fun addVirtualHosts(virtualHosts: List<String>) {
        getContextHabdler().addVirtualHosts(virtualHosts.toTypedArray())
    }

    /**
     * Remove virtual host from the running server
     */
    fun removeVirtualHosts(virtualHosts: List<String>) {
        getContextHabdler().removeVirtualHosts(virtualHosts.toTypedArray())
    }

    private fun buildContextHandler(): ContextHandler {
        val contextHandler = ContextHandler("/")
        contextHandler.virtualHosts = virtualHosts.toTypedArray()
        contextHandler.handler = httpHandler
        return contextHandler
    }

    private fun getContextHabdler(): ContextHandler {
        val handlerList = jettyServer.handler as HandlerList
        val contextHandler = handlerList.handlers[0] as ContextHandler
        return contextHandler
    }

    private fun buildServerConnector(server: Server,
                             host: String,
                             port: Int,
                             sslKeyStore: SslKeyStore?): ServerConnector {

        val httpFactory = createHttpConnectionFactory()
        val serverConnector: ServerConnector

        if (sslKeyStore == null) {
            serverConnector = ServerConnector(server, httpFactory)
        } else {
            val sslContextFactory = SslContextFactory()
            sslContextFactory.keyStoreResource = sslKeyStore.keystoreResource
            sslContextFactory.setKeyStorePassword(sslKeyStore.keystorePassword)
            serverConnector = ServerConnector(server, sslContextFactory, httpFactory)
        }
        serverConnector.idleTimeout = TimeUnit.HOURS.toMillis(1) // 3600000
        serverConnector.soLingerTime = -1 // linger time disabled
        serverConnector.host = host
        serverConnector.port = port
        return serverConnector
    }

    private fun createHttpConnectionFactory(): ConnectionFactory {
        val httpConfig = HttpConfiguration()
        httpConfig.sendServerVersion = false // do not show jetty version
        return HttpConnectionFactory(httpConfig)
    }


}



