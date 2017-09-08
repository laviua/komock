package ua.com.lavi.komock.smtp

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */
class SmtpServerProperties {

    var enabled: Boolean = false
    var name: String = "defaultInstanceName"
    var port: Int = 2525
    var hostname: String = "localhost"
    var bossThreads: Int = 1
    var workerThreads: Int = 2

    fun witHostname(hostname: String) : SmtpServerProperties {
        this.hostname = hostname
        return this
    }

    fun withPort(port: Int): SmtpServerProperties {
        this.port = port
        return this
    }

    fun withName(name: String) : SmtpServerProperties {
        this.name = name
        return this
    }
}