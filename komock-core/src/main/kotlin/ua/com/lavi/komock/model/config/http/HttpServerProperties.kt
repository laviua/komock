package ua.com.lavi.komock.model.config.http

import ua.com.lavi.komock.model.SslKeyStore

/**
 * Created by Oleksandr Loushkin
 */

open class HttpServerProperties {
    var enabled: Boolean = true
    var contextPath = "/"
    var name = "defaultInstanceName" // default name
    var virtualHosts: List<String> = ArrayList()
    var host = "0.0.0.0" // listen on all interfaces
    var port = 8080 // default port;
    var routes: List<RouteProperties> = ArrayList()
    var ssl: SSLServerProperties = SSLServerProperties()
    var capture: CaptureProperties = CaptureProperties()
    var minThreads: Int = 10
    var maxThreads: Int = 100
    var idleTimeout: Int = 60000

    fun withName(name: String): HttpServerProperties {
        this.name = name
        return this
    }

    fun withHost(host: String): HttpServerProperties {
        this.host = host
        return this
    }

    fun withPort(port: Int): HttpServerProperties {
        this.port = port
        return this
    }

    fun withSsl(sslServerProperties: SSLServerProperties): HttpServerProperties {
        this.ssl = sslServerProperties
        return this
    }
    fun withCapture(captureProperties: CaptureProperties): HttpServerProperties {
        this.capture = captureProperties
        return this
    }

    fun withRoutes(routes: List<RouteProperties>): HttpServerProperties {
        this.routes = routes
        return this
    }

    fun hasRoutes(): Boolean {
        if (routes.isEmpty()) {
            return false
        }
        return true
    }

    fun keyStore(): SslKeyStore {
        return SslKeyStore(ssl.keyStoreLocation, ssl.keyStorePassword)
    }

}
