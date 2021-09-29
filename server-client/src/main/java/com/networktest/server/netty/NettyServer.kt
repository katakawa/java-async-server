package com.networktest.server.netty

import com.networktest.HOST_PORT
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.DelimiterBasedFrameDecoder
import io.netty.handler.codec.Delimiters
import io.netty.handler.codec.string.StringDecoder
import io.netty.handler.codec.string.StringEncoder

fun main() {
    val bossGroup: EventLoopGroup = NioEventLoopGroup()
    val workerGroup: EventLoopGroup = NioEventLoopGroup()

    try {
        val serverBootstrap = ServerBootstrap()
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(socketChannel: SocketChannel) {
                        socketChannel.pipeline().addLast(DelimiterBasedFrameDecoder(1000, *Delimiters.lineDelimiter()))
                        socketChannel.pipeline().addLast(StringDecoder())
                        socketChannel.pipeline().addLast(StringEncoder())
                        socketChannel.pipeline().addLast(EchoHandler())
                    }
                })

        val server = serverBootstrap.bind(HOST_PORT).sync()
        println("Started Netty server at ${server.channel().localAddress()}")

        server.channel().closeFuture().sync()
    } finally {
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()
    }
}
