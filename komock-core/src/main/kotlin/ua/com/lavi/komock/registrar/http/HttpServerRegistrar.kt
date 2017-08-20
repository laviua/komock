package ua.com.lavi.komock.registrar.http

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.handler.MockServer
import ua.com.lavi.komock.engine.server.SecuredMockServer
import ua.com.lavi.komock.engine.server.UnsecuredMockServer
import java.net.BindException
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class HttpServerRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    //Helper object.
    companion object {

        private val ROUTERS: MutableList<MockServer> = ArrayList()

        fun startAllServers() {
            ROUTERS.forEach(MockServer::start)
        }

        fun stopAllServers() {
            ROUTERS.forEach(MockServer::stop)
        }
    }

    fun register(httpServerProperties: HttpServerProperties) {

        //SSL SecuredHttpRouter or not
        val router = if (httpServerProperties.ssl.enabled) {
            SecuredMockServer(httpServerProperties)
        } else {
            UnsecuredMockServer(httpServerProperties)
        }

        ROUTERS.add(router)

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProperties.host}, port: ${httpServerProperties.port}", e)
            return
        }

        //register only enabled routes
        httpServerProperties.routes.filter { it.enabled }.forEach { router.addRoute(it) }
    }
}
