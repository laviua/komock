package ua.com.lavi.komock.engine


import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Oleksandr Loushkin
 */

internal class JettyServer(val serverId: String,
                           val virtualHosts: ArrayList<String>,
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
    }

    fun start() {

        val contextHandler = buildContextHandler()
        val serverConnector: ServerConnector = buildServerConnector(jettyServer, host, port, sslKeyStore)

        jettyServer = serverConnector.server
        jettyServer.connectors = arrayOf(serverConnector)
        val handlerList: HandlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList

        jettyServer.start()
        log.debug("$serverId - listening on $host:$port")
    }

    fun stop() {
        log.debug("Stopping $serverId")
        jettyServer.stop()
        log.debug("$serverId is stopped")
    }

    fun addVirtualHosts(virtualHosts: ArrayList<String>) {
        val handlerList = jettyServer.handler as HandlerList
        val contextHandler = handlerList.handlers[0] as ContextHandler
        contextHandler.addVirtualHosts(virtualHosts.toTypedArray())
    }

    fun removeVirtualHosts(virtualHosts: ArrayList<String>) {
        val handlerList = jettyServer.handler as HandlerList
        val contextHandler = handlerList.handlers[0] as ContextHandler
        contextHandler.removeVirtualHosts(virtualHosts.toTypedArray())
    }

    private fun buildContextHandler(): ContextHandler {
        val contextHandler = ContextHandler("/")
        contextHandler.virtualHosts = virtualHosts.toTypedArray()
        contextHandler.handler = httpHandler
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
            val sslContextFactory = SslContextFactory(sslKeyStore.keystoreFile)
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



