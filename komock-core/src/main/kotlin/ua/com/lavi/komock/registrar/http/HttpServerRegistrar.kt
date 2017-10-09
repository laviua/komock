package ua.com.lavi.komock.registrar.http

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.model.config.http.HttpServerProperties
import ua.com.lavi.komock.http.server.MockServer
import ua.com.lavi.komock.http.server.SecuredMockServer
import ua.com.lavi.komock.http.server.UnsecuredMockServer
import ua.com.lavi.komock.registrar.Registrar
import java.net.BindException
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class HttpServerRegistrar : Registrar<HttpServerProperties>{

    private val log = LoggerFactory.getLogger(this.javaClass)

    //Helper object.
    companion object {

        private val mockServers: MutableList<MockServer> = ArrayList()

        fun getServers(): MutableList<MockServer> {
            return mockServers
        }
    }

    override fun register(properties: HttpServerProperties) {

        //SSL SecuredHttpRouter or not
        val mockServer = if (properties.ssl.enabled) {
            SecuredMockServer(properties)
        } else {
            UnsecuredMockServer(properties)
        }

        mockServers.add(mockServer)

        try {
            mockServer.start()
        } catch (e: BindException) {
            log.warn(e.message + ": ${properties.host}, port: ${properties.port}", e)
            return
        }
    }
}
