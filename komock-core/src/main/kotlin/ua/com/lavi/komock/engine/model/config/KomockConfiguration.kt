package ua.com.lavi.komock.engine.model.config

import ua.com.lavi.komock.engine.model.config.consul.ConsulAgentProperties
import ua.com.lavi.komock.engine.model.config.http.HttpServerProperties
import ua.com.lavi.komock.engine.model.config.spring.SpringConfigProperties
import java.util.*

/**
 * Created by Oleksandr Loushkin
 */

open class KomockConfiguration {

    var httpServers: List<HttpServerProperties> = ArrayList()
    var consulAgents: List<ConsulAgentProperties> = ArrayList()
    var springConfig: SpringConfigProperties = SpringConfigProperties()

}
