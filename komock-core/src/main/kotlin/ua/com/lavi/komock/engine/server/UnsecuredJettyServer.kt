package ua.com.lavi.komock.engine.server

import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.ServerConnector
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */
class UnsecuredJettyServer(serverProps: HttpServerProperties, httpHandler: HttpHandler) : AbstractJettyServer(serverProps, httpHandler) {

    override fun buildServerConnector(): ServerConnector {
        val httpFactory = HttpConnectionFactory(httpConfig())
        return ServerConnector(jettyServer, httpFactory)
    }

}