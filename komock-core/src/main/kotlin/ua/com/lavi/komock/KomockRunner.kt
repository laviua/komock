package ua.com.lavi.komock

import ua.com.lavi.komock.model.config.KomockConfiguration
import ua.com.lavi.komock.registrar.consul.ConsulRegistrar
import ua.com.lavi.komock.registrar.http.HttpServerRegistrar
import ua.com.lavi.komock.registrar.proxy.ProxyForwarder
import ua.com.lavi.komock.registrar.spring.SpringConfigRegistrar

/**
 * Created by Oleksandr Loushkin
 */

class KomockRunner {

    fun run(komockConfiguration: KomockConfiguration) {

        //Server instances
        val serverRegistrar = HttpServerRegistrar()
        komockConfiguration.httpServers
                .filter { it.enabled }
                .forEach { serverRegistrar.register(it) }

        //Spring config-server
        val springConfigRegistrar = SpringConfigRegistrar()
        val springConfigProperties = komockConfiguration.springConfig
        if (springConfigProperties.enabled) {
            springConfigRegistrar.register(springConfigProperties)
        }

        //Proxy interceptor server
        val proxyForwarder = ProxyForwarder()
        komockConfiguration.proxies
                .filter { it.enabled }
                .forEach { proxyForwarder.register(it) }

        //Consul registration
        val consulRegistrar = ConsulRegistrar()
        komockConfiguration.consulAgents
                .filter { it.enabled }
                .forEach { consulRegistrar.register(it) }
    }
}
