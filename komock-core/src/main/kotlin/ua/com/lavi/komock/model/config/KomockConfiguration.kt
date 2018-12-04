package ua.com.lavi.komock.model.config

import ua.com.lavi.komock.model.config.consul.ConsulAgentProperties
import ua.com.lavi.komock.model.config.http.HttpServerProperties
import ua.com.lavi.komock.model.config.proxy.ProxyConfigProperties
import ua.com.lavi.komock.model.config.spring.SpringConfigProperties

/**
 * Created by Oleksandr Loushkin
 */

open class KomockConfiguration {

    var httpServers: List<HttpServerProperties> = ArrayList()
    var consulAgents: List<ConsulAgentProperties> = ArrayList()
    var springConfig: SpringConfigProperties = SpringConfigProperties()
    var proxies: List<ProxyConfigProperties> = ArrayList()

}
