package ua.com.lavi.smtpgate.netty

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.net.InetSocketAddress
import java.util.concurrent.Executors

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */

class SmtpServer(val serverProperties: SmtpServerProperties) {

    val smtpServerHandler = SmtpServerHandler(serverProperties)

    var bootstrap: ServerBootstrap = ServerBootstrap(
            NioServerSocketChannelFactory(
                    Executors.newFixedThreadPool(serverProperties.bossThreads),
                    Executors.newFixedThreadPool(serverProperties.workerThreads)))

    fun start() {
        bootstrap.pipelineFactory = SmtpServerPipelineFactory(smtpServerHandler)
        bootstrap.bind(InetSocketAddress(serverProperties.hostname, serverProperties.port))
    }

    fun stop() {
        bootstrap.shutdown()
    }

    fun getMessages() : List<String> {
        return smtpServerHandler.storedMessages
    }
}