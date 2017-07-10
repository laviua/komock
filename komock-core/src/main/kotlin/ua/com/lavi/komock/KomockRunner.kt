package ua.com.lavi.komock

import ua.com.lavi.komock.engine.model.config.KomockConfiguration
import ua.com.lavi.komock.registrar.ConsulRegistrar
import ua.com.lavi.komock.registrar.ServerRegistrar
import ua.com.lavi.komock.registrar.spring.SpringConfigRegistrar

/**
 * Created by Oleksandr Loushkin
 */

class KomockRunner {

    fun run(komockConfiguration: KomockConfiguration) {

        //Server instances
        val serverRegistrar = ServerRegistrar()
        komockConfiguration.httpServers.
                filter { it.enabled }.
                forEach { serverRegistrar.register(it) }

        //Spring config-server
        val springConfigRegistrar = SpringConfigRegistrar()
        val springConfigProperties = komockConfiguration.springConfig
        if (springConfigProperties.enabled) {
            springConfigRegistrar.register(springConfigProperties)
        }

        //Consul registration
        val consulRegistrar = ConsulRegistrar()
        komockConfiguration.consulAgents.filter { it.enabled }.forEach { consulRegistrar.register(it) }
    }
}
