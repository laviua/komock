package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */
class SecuredJettyServer(serverProps: HttpServerProperties, httpHandler: HttpHandler) : AbstractJettyServer(serverProps, httpHandler) {

    override fun buildServerConnector(): ServerConnector {
        val httpFactory = HttpConnectionFactory(httpConfig())
        val serverConnector: ServerConnector
        val sslContextFactory = SslContextFactory()

        val sslKeyStore = serverProps.keyStore()
        sslContextFactory.keyStoreResource = sslKeyStore.keystoreResource
        sslContextFactory.setKeyStorePassword(sslKeyStore.keystorePassword)
        serverConnector = ServerConnector(jettyServer, sslContextFactory, httpFactory)
        return serverConnector
    }
}