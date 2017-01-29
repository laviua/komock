package ua.com.lavi.komock.config.property.consul

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class ConsulServerProperties {
    var enabled: Boolean = false
    var consulHost = "localhost"
    var services = ArrayList<ConsulServiceProperties>()
}

class ConsulServiceProperties {
    var serviceId: String = "defaultConsulService"
    var serviceName: String = "defaultConsulServiceName"
    var servicePort: Int = 8080
    var serviceAddress: String = "127.0.0.1"
    var checkInterval: String = "30s"
    var checkTimeout: String? = "30s"
}