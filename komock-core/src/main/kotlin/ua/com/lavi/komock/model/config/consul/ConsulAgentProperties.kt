package ua.com.lavi.komock.model.config.consul

import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class ConsulAgentProperties {
    var enabled: Boolean = false
    var consulHost = "localhost"
    var consulPort = 8500
    var daemon: Boolean = false
    var services: List<ConsulServiceAgentProperties> = ArrayList()
}