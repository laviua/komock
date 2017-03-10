package ua.com.lavi.komock.engine.model.config.consul

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class ConsulAgentProperties {
    open var enabled: Boolean = false
    open var consulHost = "localhost"
    open var consulPort = 8500
    open var services: List<ConsulServiceAgentProperties> = ArrayList()
}

open class ConsulServiceAgentProperties {
    open var enabled: Boolean = true
    open var serviceId: String = "defaultConsulService"
    open var serviceName: String = "defaultConsulServiceName"
    open var servicePort: Int = 8080
    open var serviceAddress: String = "127.0.0.1"
    open var checkInterval: String = "30s"
    open var checkTimeout: String = "30s"
    open var tcp: String? = null
    open var http: String? = null
    open var script: String? = null
}