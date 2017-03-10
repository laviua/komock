package ua.com.lavi.komock.engine.model.config.http

import java.util.*


/**
 * Created by Oleksandr Loushkin
 */

open class HttpServerProperties {
    open var enabled: Boolean = true
    open var name = "defaultInstanceName" // default name
    open var virtualHosts:List<String> = ArrayList()
    open var host = "0.0.0.0" // listen on all interfaces
    open var port = 8080 // default port;
    open var routes:List<RouteProperties> = ArrayList()
    open var ssl: SSLServerProperties = SSLServerProperties()
    open var minThreads: Int = 10
    open var maxThreads: Int = 100
    open var idleTimeout: Int = 60000
}

open class SSLServerProperties {
    open var enabled: Boolean = false
    open var keyStoreLocation: String = "keystore.jks"
    open var keyStorePassword: String = "password"
}
