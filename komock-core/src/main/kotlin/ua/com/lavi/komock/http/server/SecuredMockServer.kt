package ua.com.lavi.komock.http.server

import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import ua.com.lavi.komock.model.config.http.HttpServerProperties

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */

class SecuredMockServer(serverProps: HttpServerProperties) : AbstractMockServer(serverProps) {

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