package ua.com.lavi.komock

import ua.com.lavi.komock.engine.model.config.KomockConfiguration
import ua.com.lavi.komock.registrar.ConsulRegistrar
import ua.com.lavi.komock.registrar.ServerRegistrar
import ua.com.lavi.komock.registrar.SpringConfigRegistrar

/**
 * Created by Oleksandr Loushkin
 */

class KomockRunner {

    fun run(komockConfiguration: KomockConfiguration) {

        //Server instances
        val serverRegistrar = ServerRegistrar()
        komockConfiguration.servers.
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
        val consulServerProperties = komockConfiguration.consul
        if (consulServerProperties.enabled) {
            consulRegistrar.register(consulServerProperties)
        }
    }
}
