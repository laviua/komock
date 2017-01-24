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

internal class JettyServer(val serverId: String, val virtualHosts: ArrayList<String>, val httpHandler: Handler) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var jettyServer: Server

    fun start(host: String,
              port: Int,
              sslKeyStore: SslKeyStore?,
              maxThreads: Int,
              minThreads: Int,
              idleTimeout: Int) {

        jettyServer = Server(QueuedThreadPool(maxThreads, minThreads, idleTimeout))

        val contextHandler = ContextHandler("/")
        contextHandler.virtualHosts = virtualHosts.toTypedArray()
        contextHandler.handler = httpHandler

        val connector: ServerConnector = buildSocketConnector(jettyServer, host, port, sslKeyStore)
        jettyServer = connector.server
        jettyServer.connectors = arrayOf(connector)
        val handlerList: HandlerList = HandlerList()
        handlerList.handlers = arrayOf(contextHandler)
        jettyServer.handler = handlerList

        try {
            jettyServer.start()
            jettyServer.join()
            log.info("$serverId - listening on $host:$port")
        } catch (e: Exception) {
            log.error("$serverId - start failed", e)
            System.exit(100)
        }

    }

    fun stop() {
        log.info("Stopping $serverId")
        jettyServer.stop()
        log.info("$serverId is stopped")
    }

    fun buildSocketConnector(server: Server,
                             host: String,
                             port: Int,
                             sslKeyStore: SslKeyStore?): ServerConnector {

        val httpConfig = HttpConfiguration()
        httpConfig.sendServerVersion = false
        val httpFactory = HttpConnectionFactory(httpConfig)

        val connector: ServerConnector?
        if (sslKeyStore == null) {
            connector = ServerConnector(server, httpFactory)
        } else {
            val sslContextFactory = SslContextFactory(sslKeyStore.keystoreFile)
            sslContextFactory.setKeyStorePassword(sslKeyStore.keystorePassword)
            connector = ServerConnector(server, sslContextFactory, httpFactory)
        }
        connector.idleTimeout = TimeUnit.HOURS.toMillis(1)
        connector.soLingerTime = -1
        connector.host = host
        connector.port = port
        return connector
    }


}



