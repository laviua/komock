package ua.com.lavi.komock

import ua.com.lavi.komock.config.ApplicationConfiguration
import ua.com.lavi.komock.registrar.ConsulRegistrar
import ua.com.lavi.komock.registrar.ServerRegistrar
import ua.com.lavi.komock.registrar.SpringConfigRegistrar

/**
 * Created by Oleksandr Loushkin
 */

class KomockRunner {

    fun run(applicationConfiguration: ApplicationConfiguration) {

        //Server instances
        val serverRegistrar = ServerRegistrar()
        applicationConfiguration.servers.
                filter { it.enabled }.
                forEach { serverRegistrar.register(it) }

        //Spring config-server
        val springConfigRegistrar = SpringConfigRegistrar()
        val springConfigProperties = applicationConfiguration.springConfig
        if (springConfigProperties.enabled) {
            springConfigRegistrar.register(springConfigProperties)
        }

        //Consul registration
        val consulRegistrar = ConsulRegistrar()
        val consulServerProperties = applicationConfiguration.consul
        if (consulServerProperties.enabled) {
            consulRegistrar.register(consulServerProperties)
        }
    }
}
