package ua.com.lavi.komock.registrar

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.config.property.http.ServerProperties
import ua.com.lavi.komock.engine.model.SslKeyStore

/**
 * Created by Oleksandr Loushkin
 */

class ServerRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun registerServer(serverProp: ServerProperties) {

        var sslKeyStore: SslKeyStore? = null
        if (serverProp.secure.enabled) {
            sslKeyStore = SslKeyStore(serverProp.secure.keyStoreLocation, serverProp.secure.keyStorePassword)
        }
        val router = Router(serverProp.id,
                serverProp.ipAddress, serverProp.port,
                serverProp.minThreads, serverProp.maxThreads,
                serverProp.idleTimeout, sslKeyStore, serverProp.virtualHosts)

        router.start()

        log.info("Started server: ${serverProp.id} on port: ${serverProp.port}. virtualHosts: ${serverProp.virtualHosts.joinToString(",")}")
        log.info("maxThreads: ${serverProp.maxThreads}. minThreads: ${serverProp.minThreads}. idle timeout: ${serverProp.idleTimeout} ms")

        //register routeHolders
        if (!serverProp.routes.isEmpty()) {
            serverProp.routes.forEach { router.addRoute(it) }
        }
    }
}
