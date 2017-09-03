package ua.com.lavi.smtpgate.netty

import org.jboss.netty.channel.ChannelPipeline
import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.channel.DefaultChannelPipeline
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder
import org.jboss.netty.handler.codec.frame.Delimiters
import org.jboss.netty.handler.codec.string.StringDecoder
import org.jboss.netty.handler.codec.string.StringEncoder

/**
 * Created by Oleksandr Loushkin on 03.09.17.
 */


class SmtpServerPipelineFactory(val smtpServerHandler: SmtpServerHandler) : ChannelPipelineFactory {

    override fun getPipeline(): ChannelPipeline {
        val pipeline = DefaultChannelPipeline()
        pipeline.addLast("framer", DelimiterBasedFrameDecoder(1000, *Delimiters.lineDelimiter()))
        pipeline.addLast("decoder", StringDecoder(Charsets.UTF_8))
        pipeline.addLast("encoder", StringEncoder())
        pipeline.addLast("handler", smtpServerHandler)
        return pipeline
    }
}