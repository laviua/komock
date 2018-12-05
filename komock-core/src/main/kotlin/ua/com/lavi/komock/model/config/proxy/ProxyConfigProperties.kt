package ua.com.lavi.komock.model.config.proxy

import ua.com.lavi.komock.model.config.http.HttpServerProperties

open class ProxyConfigProperties {
    var enabled: Boolean = false
    var httpServer: HttpServerProperties = HttpServerProperties()
    var forwardTo: String = ""
    var writeTo: String = ""
    var connectTimeout: Int = 250000
    var connectionRequestTimeout: Int = 30000
    var socketTimeout: Int = 25000
}