package ua.com.lavi.komock.engine


import org.eclipse.jetty.server.Connector
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.util.concurrent.TimeUnit

/**
 * Created by Oleksandr Loushkin
 */

internal class JettyServer(val serverId: String, val handler: Handler) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private lateinit var server: Server

    fun start(host: String,
              port: Int,
              sslKeyStore: SslKeyStore?,
              maxThreads: Int,
              minThreads: Int,
              idleTimeout: Int) {

        server = Server(QueuedThreadPool(maxThreads, minThreads, idleTimeout))

        val connector: ServerConnector = buildSocketConnector(server, host, port, sslKeyStore)
        server = connector.server
        server.connectors = arrayOf<Connector>(connector)
        val handlerList: HandlerList = HandlerList()
        handlerList.handlers = arrayOf(handler)
        server.handler = handlerList

        try {
            server.start()
            server.join()
            log.info("$serverId - listening on $host:$port")
        } catch (e: Exception) {
            log.error("$serverId - start failed", e)
            System.exit(100)
        }

    }

    fun stop() {
        log.info("Stopping $serverId")
        server.stop()
        log.info("$serverId is stopped")
    }

    fun buildSocketConnector(server: Server,
                             host: String,
                             port: Int,
                             sslKeyStore: SslKeyStore?): ServerConnector {

        val connector: ServerConnector?
        if (sslKeyStore == null) {
            connector = ServerConnector(server)
        } else {
            val sslContextFactory = SslContextFactory(sslKeyStore.keystoreFile)
            sslContextFactory.setKeyStorePassword(sslKeyStore.keystorePassword)
            connector = ServerConnector(server, sslContextFactory)
        }
        connector.idleTimeout = TimeUnit.HOURS.toMillis(1)
        connector.soLingerTime = -1
        connector.host = host
        connector.port = port
        return connector
    }


}



