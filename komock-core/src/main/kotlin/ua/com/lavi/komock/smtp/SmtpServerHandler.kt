package ua.com.lavi.komock.smtp

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */

import io.netty.channel.*
import ua.com.lavi.komock.model.config.smtp.SmtpServerProperties
import ua.com.lavi.komock.ext.toByteArrayInputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.MimeMessage

class SmtpServerHandler(val smtpMailbox: SmtpMailbox, serverProps: SmtpServerProperties) : ChannelInboundHandlerAdapter() {

    private var hostname: String = serverProps.hostname
    private var dataMode = false
    private val dataBuffer = StringBuilder()

    //filter
    override fun channelRegistered(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()
        dataBuffer.setLength(0) // reset buffer
        channel.writeAndFlush("220 $hostname ESMTP\r\n")
        super.channelRegistered(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        val channel = ctx.channel()
        val data = msg as String

        if (!dataMode) {
            commandHandler(channel, data)
        } else {
            dataHandler(channel, data)
        }
    }

    private fun commandHandler(channel: Channel, data: String) {
        val receivedCommand: String
        val i = data.indexOf(" ")
        if (i > 0) {
            receivedCommand = data.substring(0, i).toUpperCase()
        } else {
            receivedCommand = data.toUpperCase()
        }

        if (receivedCommand.isEmpty()) {
            channel.writeAndFlush("500 Error: bad syntax\r\n")
            return
        }

        if (receivedCommand == "HELO") {
            channel.writeAndFlush("250 " + hostname + "\r\n")
        } else if (receivedCommand == "EHLO") {
            channel.writeAndFlush("250 " + hostname + "\r\n")
        } else if (receivedCommand == "MAIL") { // start data
            channel.writeAndFlush("250 OK\r\n")
        } else if (receivedCommand == "RCPT") {
            channel.writeAndFlush("250 OK\r\n")
        } else if (receivedCommand == "DATA") {
            dataMode = true
            channel.writeAndFlush("354 End data with <CR><LF>.<CR><LF>\r\n")
        } else if (receivedCommand == "RSET") {
            channel.writeAndFlush("250 OK\r\n")
        } else if (receivedCommand == "QUIT") {
            channel.writeAndFlush("221 $hostname closing connection\r\n").addListener(ChannelFutureListener.CLOSE)
            val mimeMessage = MimeMessage(Session.getDefaultInstance(Properties()), dataBuffer.toByteArrayInputStream())
            smtpMailbox.deliver(mimeMessage)
        } else {
            channel.writeAndFlush("500 unrecognized receivedCommand\r\n")
        }
    }

    private fun dataHandler(channel: Channel, data: String) {
        var line = data
        if (line.trim { it <= ' ' } == ".") {
            // end-of-data
            dataMode = false
            channel.writeAndFlush("250 OK\r\n")
        } else {
            // unescape leading dot
            if (line.startsWith("..")) {
                line = line.substring(1)
            }
            dataBuffer.append(line).append("\n")
        }
    }
}

