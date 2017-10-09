package ua.com.lavi.komock.registrar.smtp

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.registrar.Registrar
import ua.com.lavi.komock.smtp.SmtpServer
import ua.com.lavi.komock.model.config.smtp.SmtpServerProperties
import java.net.BindException
import java.util.*

/**
 * Created by Oleksandr Loushkin on 09.09.17.
 */
class SmtpServerRegistrar : Registrar<SmtpServerProperties> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    //Helper object.
    companion object {

        private val smtpServers: MutableList<SmtpServer> = ArrayList()

        fun getServers(): MutableList<SmtpServer> {
            return smtpServers
        }
    }

    override fun register(properties: SmtpServerProperties) {
        val smtpServer = SmtpServer(properties)

        try {
            smtpServer.start()
            smtpServers.add(smtpServer)
        } catch (e: BindException) {
            log.warn(e.message + ": ${properties.hostname}, port: ${properties.port}", e)
            return
        }
    }
}