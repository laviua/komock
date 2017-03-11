package ua.com.lavi.komock.engine.model.config.http

import java.util.*


/**
 * Created by Oleksandr Loushkin
 */

open class HttpServerProperties {
    var enabled: Boolean = true
    var name = "defaultInstanceName" // default name
    var virtualHosts:List<String> = ArrayList()
    var host = "0.0.0.0" // listen on all interfaces
    var port = 8080 // default port;
    var routes:List<RouteProperties> = ArrayList()
    var ssl: SSLServerProperties = SSLServerProperties()
    var minThreads: Int = 10
    var maxThreads: Int = 100
    var idleTimeout: Int = 60000
}
