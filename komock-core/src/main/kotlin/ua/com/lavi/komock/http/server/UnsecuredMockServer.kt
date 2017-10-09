package ua.com.lavi.komock.http.server

import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.ServerConnector
import ua.com.lavi.komock.model.config.http.HttpServerProperties

/**
 * Created by Oleksandr Loushkin on 19.08.17.
 */

class UnsecuredMockServer(serverProps: HttpServerProperties) : AbstractMockServer(serverProps) {

    override fun buildServerConnector(): ServerConnector {
        val httpFactory = HttpConnectionFactory(httpConfig())
        return ServerConnector(jettyServer, httpFactory)
    }

}