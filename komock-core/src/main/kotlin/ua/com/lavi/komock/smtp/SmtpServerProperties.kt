package ua.com.lavi.smtpgate.netty

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */
class SmtpServerProperties {

    var port: Int = 2525
    var hostname: String = "localhost"
    var bossThreads: Int = 1
    var workerThreads: Int = 2

    fun withPort(port: Int): SmtpServerProperties {
        this.port = port
        return this
    }
}