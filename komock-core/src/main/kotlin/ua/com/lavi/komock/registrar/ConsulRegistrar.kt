package ua.com.lavi.komock.registrar

import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.agent.model.NewService
import org.slf4j.LoggerFactory
import ua.com.lavi.komock.engine.model.config.property.consul.ConsulAgentProperties

/**
 * Created by Oleksandr Loushkin
 */

class ConsulRegistrar {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun register(consulAgentProperties: ConsulAgentProperties) {
        val clientRegistrar = ConsulClient(consulAgentProperties.consulHost, consulAgentProperties.consulPort)
        log.debug("Found: ${consulAgentProperties.services.size} consul services")
        for (consulService in consulAgentProperties.services) {
            val newService = NewService()
            newService.id = consulService.serviceId
            newService.name = consulService.serviceName
            newService.port = consulService.servicePort
            newService.address = consulService.serviceAddress
            val check = NewService.Check()
                check.interval = consulService.checkInterval
                check.timeout = consulService.checkTimeout
                check.tcp = consulService.tcp
                check.http = consulService.http
                check.script = consulService.script
            newService.check = check
            clientRegistrar.agentServiceRegister(newService)
            log.info("Registered consul service: ${consulService.serviceId} - ${consulService.serviceAddress}:${consulService.servicePort}")
        }
        daemonMode()
    }

    private fun daemonMode() {
        try {
            log.info("Consul registration is running in daemon mode")
            Thread.currentThread().join()
        } catch (e: InterruptedException) {
            log.warn("Error: {}", e)
        }
    }

}
