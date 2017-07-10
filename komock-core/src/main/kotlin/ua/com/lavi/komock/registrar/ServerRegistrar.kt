package ua.com.lavi.komock.registrar

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.engine.model.ByteResource
import ua.com.lavi.komock.engine.model.SslKeyStore
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class ServerRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)


    //Helper object.
    companion object {

        private val routers:MutableList<Router> = ArrayList()

        @JvmStatic
        fun startAllServers() {
            routers.forEach(Router::start)
        }

        @JvmStatic
        fun stopAllServers() {
            routers.forEach(Router::stop)
        }
    }

    fun register(httpServerProp: HttpServerProperties) {

        var sslKeyStore: SslKeyStore? = null
        if (httpServerProp.ssl.enabled) {
            sslKeyStore = SslKeyStore(
                    ByteResource(Files.readAllBytes(Paths.get(httpServerProp.ssl.keyStoreLocation))),
                    httpServerProp.ssl.keyStorePassword)
        }
        val router = Router(httpServerProp, sslKeyStore)

        routers.add(router)

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProp.host}, port: ${httpServerProp.port}", e)
            return
        }

        //register only enabled routeHolders
        if (!httpServerProp.routes.isEmpty()) {
            httpServerProp.routes.filter { it.enabled }.forEach { router.addRoute(it) }
        }
    }
}
