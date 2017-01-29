package ua.com.lavi.komock.config

import ua.com.lavi.komock.config.property.consul.ConsulServerProperties
import ua.com.lavi.komock.config.property.http.ServerProperties
import ua.com.lavi.komock.config.property.spring.SpringConfigProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class ApplicationConfiguration {

    var servers: ArrayList<ServerProperties> = ArrayList()
    var consul: ConsulServerProperties = ConsulServerProperties()
    var springConfig: SpringConfigProperties = SpringConfigProperties()
    var daemonMode = false

}
