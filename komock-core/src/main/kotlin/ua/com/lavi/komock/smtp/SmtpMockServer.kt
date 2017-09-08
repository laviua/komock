package ua.com.lavi.komock.smtp

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder
import org.slf4j.LoggerFactory
import javax.mail.internet.MimeMessage

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */

class SmtpMockServer(val serverProps: SmtpServerProperties) {
    private var started = false
    private val log = LoggerFactory.getLogger(this.javaClass)

    val smtpServerHandler = SmtpServerHandler(serverProps)
    var bossGroup = NioEventLoopGroup(serverProps.bossThreads)
    var workerGroup = NioEventLoopGroup(serverProps.workerThreads)
    var channel: ChannelFuture? = null

    fun getMessages(): List<MimeMessage> {
        return smtpServerHandler.getMessages()
    }

    fun start() {
        if (started) {
            log.warn("Server is already started.")
            return
        }
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                stop()
            }
        })
        log.info("Starting server: ${serverProps.name}")
        started = true
        val serverBootstrap = ServerBootstrap()
        serverBootstrap.group(bossGroup, workerGroup)
        serverBootstrap.channel(NioServerSocketChannel::class.java)
        serverBootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            public override fun initChannel(socketChannel: SocketChannel) {
                val pipeline = socketChannel.pipeline()
                //pipeline.addLast(LoggingHandler())
                pipeline.addLast(DelimiterBasedFrameDecoder(8192, *Delimiters.lineDelimiter()))
                pipeline.addLast(StringDecoder())
                pipeline.addLast(StringEncoder())
                pipeline.addLast(smtpServerHandler)
            }
        })

        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128)
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)

        channel = serverBootstrap.bind(serverProps.port).sync()

        log.info("Started server: ${serverProps.name} on port: ${serverProps.port}. " +
                "Boss threads: ${serverProps.bossThreads}. " +
                "Worker threads: ${serverProps.workerThreads}")
    }

    fun stop() {
        log.info("Stopping server: ${serverProps.name}")
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
        channel!!.channel().closeFuture().sync()
        log.info("Stopped server: ${serverProps.name}")
    }

}