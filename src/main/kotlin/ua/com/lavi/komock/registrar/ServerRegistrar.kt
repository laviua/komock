package ua.com.lavi.komock.registrar

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.config.property.http.ServerProperties
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.net.BindException

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
                serverProp.host, serverProp.port,
                serverProp.minThreads, serverProp.maxThreads,
                serverProp.idleTimeout, sslKeyStore, serverProp.virtualHosts)

        try {
            router.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${serverProp.host}, port: ${serverProp.port}", e)
            return
        }

        //register routeHolders
        if (!serverProp.routes.isEmpty()) {
            serverProp.routes.forEach { router.addRoute(it) }
        }
    }
}
