package ua.com.lavi.komock.registrar.http

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.server.MockServer
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

        private val mockServers: MutableList<MockServer> = ArrayList()

        fun startAllServers() {
            mockServers.forEach(MockServer::start)
        }

        fun stopAllServers() {
            mockServers.forEach(MockServer::stop)
        }

        fun getServers(): MutableList<MockServer> {
            return mockServers
        }
    }

    fun register(httpServerProperties: HttpServerProperties) {

        //SSL SecuredHttpRouter or not
        val mockServer = if (httpServerProperties.ssl.enabled) {
            SecuredMockServer(httpServerProperties)
        } else {
            UnsecuredMockServer(httpServerProperties)
        }

        mockServers.add(mockServer)

        try {
            mockServer.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${httpServerProperties.host}, port: ${httpServerProperties.port}", e)
            return
        }
    }
}
