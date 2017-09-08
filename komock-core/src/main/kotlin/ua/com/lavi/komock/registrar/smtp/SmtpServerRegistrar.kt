package ua.com.lavi.komock.registrar.smtp

import org.slf4j.LoggerFactory
import ua.com.lavi.komock.registrar.Registrar
import ua.com.lavi.komock.smtp.SmtpServer
import ua.com.lavi.komock.smtp.SmtpServerProperties
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

    override fun register(smtpServerProperties: SmtpServerProperties) {
        val smtpServer = SmtpServer(smtpServerProperties)

        try {
            smtpServer.start()
            smtpServers.add(smtpServer)
        } catch (e: BindException) {
            log.warn(e.message + ": ${smtpServerProperties.hostname}, port: ${smtpServerProperties.port}", e)
            return
        }
    }
}