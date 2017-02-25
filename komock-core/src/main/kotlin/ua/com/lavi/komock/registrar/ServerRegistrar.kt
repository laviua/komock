package ua.com.lavi.komock.registrar

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.Router
import ua.com.lavi.komock.engine.model.ByteResource
import ua.com.lavi.komock.engine.model.config.property.http.ServerProperties
import ua.com.lavi.komock.engine.model.SslKeyStore
import java.net.BindException
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by Oleksandr Loushkin
 */

class ServerRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun register(serverProp: ServerProperties) {

        var sslKeyStore: SslKeyStore? = null
        if (serverProp.ssl.enabled) {
            sslKeyStore = SslKeyStore(
                    ByteResource(Files.readAllBytes(Paths.get(serverProp.ssl.keyStoreLocation))),
                    serverProp.ssl.keyStorePassword)
        }
        val router = Router(serverProp.name,
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
