package ua.com.lavi.smtpgate.netty

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */

import org.jboss.netty.channel.*

class SmtpServerHandler(serverProperties: SmtpServerProperties) : SimpleChannelHandler() {
    private var hostname: String = serverProperties.hostname
    private var dataMode = false
    private val dataBuffer = StringBuffer()
    val storedMessages: MutableList<String> = ArrayList()

    //filter
    override fun channelOpen(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        super.channelOpen(ctx, e)
    }

    //send welcome banner
    override fun channelConnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) {
        val channel = e.channel
        dataBuffer.setLength(0) // reset buffer
        channel.write("220 $hostname ESMTP\r\n")
    }

    override fun messageReceived(ctx: ChannelHandlerContext, messageEvent: MessageEvent) {
        val channel = messageEvent.channel
        val data = messageEvent.message as String

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
            channel.write("500 Error: bad syntax\r\n")
            return
        }

        if (receivedCommand == "HELO") {
            channel.write("250 " + hostname + "\r\n")
        } else if (receivedCommand == "EHLO") {
            channel.write("250 " + hostname + "\r\n")
        } else if (receivedCommand == "MAIL") { // start data
            channel.write("250 OK\r\n")
        } else if (receivedCommand == "RCPT") {
            channel.write("250 OK\r\n")
        } else if (receivedCommand == "DATA") {
            dataMode = true
            channel.write("354 End data with <CR><LF>.<CR><LF>\r\n")
        } else if (receivedCommand == "RSET") {
            channel.write("250 OK\r\n")
        } else if (receivedCommand == "QUIT") {
            val channelFuture = channel.write("221 $hostname closing connection\r\n")
            channelFuture.addListener(ChannelFutureListener.CLOSE)
        } else {
            channel.write("500 unrecognized receivedCommand\r\n")
        }
    }

    private fun dataHandler(channel: Channel, data: String) {
        var line = data
        if (line.trim { it <= ' ' } == ".") {
            // end-of-data
            dataMode = false
            channel.write("250 OK\r\n")
        } else {
            // unescape leading dot
            if (line.startsWith("..")) {
                line = line.substring(1)
            }
            dataBuffer.append(line)
        }
    }

    override fun channelDisconnected(ctx: ChannelHandlerContext?, e: ChannelStateEvent?) {
        storedMessages.add(dataBuffer.toString())
        super.channelDisconnected(ctx, e)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
        e.cause.printStackTrace()
        val ch = e.channel
        ch.close()
    }
}
