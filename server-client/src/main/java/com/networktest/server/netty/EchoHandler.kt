package com.networktest.server.netty

import com.networktest.LINE_SEPARATOR
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.time.LocalDateTime

class EchoHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        println("Received message: $msg at: ${LocalDateTime.now()}")
        ctx.write("$msg $LINE_SEPARATOR")
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
    }
}