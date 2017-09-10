package ua.com.lavi.komock.model.config.consul

/**
 * Created by Oleksandr Loushkin
 */

open class ConsulServiceAgentProperties {
    var enabled: Boolean = true
    var serviceId: String = "consulService"
    var serviceName: String = "consulServiceName"
    var servicePort: Int = 8080
    var serviceAddress: String = "127.0.0.1"
    var checkInterval: String = "30s"
    var checkTimeout: String = "30s"
    var tcp: String? = null
    var http: String? = null
    var script: String? = null
}