package ua.com.lavi.komock.engine.model.config

import ua.com.lavi.komock.engine.model.config.property.consul.ConsulAgentProperties
import ua.com.lavi.komock.engine.model.config.property.http.ServerProperties
import ua.com.lavi.komock.engine.model.config.property.spring.SpringConfigProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

class KomockConfiguration {

    var servers: List<ServerProperties> = ArrayList()
    var consul: ConsulAgentProperties = ConsulAgentProperties()
    var springConfig: SpringConfigProperties = SpringConfigProperties()

}
