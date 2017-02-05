package ua.com.lavi.komock.config.property.http

import java.util.*


/**
 * Created by Oleksandr Loushkin
 */

class ServerProperties {
    var enabled: Boolean = true
    var id = "defaultInstance" // default name
    var virtualHosts = ArrayList<String>()
    var host = "0.0.0.0" // listen on all interfaces
    var port = 8080 // default port;
    var routes = ArrayList<RouteProperties>()
    var secure: SecureServerProperties = SecureServerProperties()
    var minThreads: Int = 10
    var maxThreads: Int = 100
    var idleTimeout: Int = 60000
}

class SecureServerProperties {
    var enabled: Boolean = false
    var keyStoreLocation: String = "keystore.jks"
    var keyStorePassword: String = "passworwd"
}
