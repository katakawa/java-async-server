package com.networktest.server.nio

import com.networktest.HOST_PORT
import com.networktest.LINE_SEPARATOR
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

private val CLIENT_SOCKET_CHANNELS: MutableMap<SocketChannel, ByteBuffer> = ConcurrentHashMap()

fun main() {
    val serverChannel = ServerSocketChannel.open()
    serverChannel.socket().bind(InetSocketAddress(HOST_PORT))
    serverChannel.configureBlocking(false)
    val selector = Selector.open()

    serverChannel.register(selector, SelectionKey.OP_ACCEPT)
    println("Server started at port $HOST_PORT")

    while (true) {
        selector.select()
        for (key in selector.selectedKeys()) {
            if (key.isValid) {
                when {
                    key.isAcceptable -> acceptConnection(serverChannel, selector)
                    key.isReadable -> acceptRead(selector, key)
                    key.isWritable -> acceptWrite(selector, key)
                }
            }
        }
        selector.selectedKeys().clear()
    }
}

private fun acceptConnection(serverChannel: ServerSocketChannel, selector: Selector) {
    val socketChannel = serverChannel.accept()
    socketChannel.configureBlocking(false)
    println("Connected ${socketChannel.remoteAddress}")
    CLIENT_SOCKET_CHANNELS[socketChannel] = ByteBuffer.allocate(1000)
    socketChannel.register(selector, SelectionKey.OP_READ)
}

private fun acceptRead(selector: Selector, key: SelectionKey) {
    val socketChannel = key.channel() as SocketChannel
    val buffer = CLIENT_SOCKET_CHANNELS[socketChannel]
    val bytesRead = socketChannel.read(buffer)
    println("Reading from: ${socketChannel.remoteAddress} bytes received: $bytesRead")

    if (bytesRead == -1) {
        println("Closing connection with ${socketChannel.remoteAddress}")
        CLIENT_SOCKET_CHANNELS.remove(socketChannel)
        socketChannel.close()
    }

    if (bytesRead > 0 && buffer!![buffer.position() - 1] == LINE_SEPARATOR.toByte()) {
        socketChannel.register(selector, SelectionKey.OP_WRITE)
    }
}

private fun acceptWrite(selector: Selector, key: SelectionKey) {
    val socketChannel = key.channel() as SocketChannel
    val buffer = CLIENT_SOCKET_CHANNELS[socketChannel]

    buffer!!.flip()
    val clientMessage = String(buffer.array(), buffer.position(), buffer.limit())

    buffer.clear()
    buffer.put(ByteBuffer.wrap(clientMessage.toByteArray()))
    buffer.flip()

    val bytesWritten = socketChannel.write(buffer)
    println("Writing to ${socketChannel.remoteAddress}, bytes sent $bytesWritten")

    if (!buffer.hasRemaining()) {
        buffer.compact()
        socketChannel.register(selector, SelectionKey.OP_READ)
    }
}
