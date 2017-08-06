package ua.com.lavi.komock.registrar.http

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.router.HttpRouter
import ua.com.lavi.komock.engine.router.SecuredHttpRouter
import ua.com.lavi.komock.engine.router.UnsecuredHttpRouter
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.net.BindException
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class HttpServerRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    //Helper object.
    companion object {

        private val routers: MutableList<HttpRouter> = ArrayList()

        @JvmStatic
        fun startAllServers() {
            routers.forEach(HttpRouter::start)
        }

        @JvmStatic
        fun stopAllServers() {
            routers.forEach(HttpRouter::stop)
        }
    }

    fun register(httpServerProperties: HttpServerProperties) {

        //SSL SecuredHttpRouter or not
        val router = if (httpServerProperties.ssl.enabled) {
            SecuredHttpRouter(httpServerProperties, SslKeyStore(httpServerProperties.ssl.keyStoreLocation, httpServerProperties.ssl.keyStorePassword))
        } else {
            UnsecuredHttpRouter(httpServerProperties)
        }

        routers.add(router)

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProperties.host}, port: ${httpServerProperties.port}", e)
            return
        }

        //register only enabled routes
        if (!httpServerProperties.routes.isEmpty()) {
            httpServerProperties.routes.filter { it.enabled }.forEach { router.addRoute(it) }
        }
    }
}
