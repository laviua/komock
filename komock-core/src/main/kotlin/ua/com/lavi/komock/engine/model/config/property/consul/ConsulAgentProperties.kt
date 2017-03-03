package ua.com.lavi.komock.engine.model.config.property.consul

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class ConsulAgentProperties {
    var id: Long? = null
    var enabled: Boolean = false
    var consulHost = "localhost"
    var consulPort = 8500
    var services: List<ConsulServiceAgentProperties> = ArrayList()
}

class ConsulServiceAgentProperties {
    var id: Long? = null
    var enabled: Boolean = true
    var serviceId: String = "defaultConsulService"
    var serviceName: String = "defaultConsulServiceName"
    var servicePort: Int = 8080
    var serviceAddress: String = "127.0.0.1"
    var checkInterval: String = "30s"
    var checkTimeout: String = "30s"
    var tcp: String? = null
    var http: String? = null
    var script: String? = null
}