package com.networktest.server.socket

import com.networktest.HOST_PORT
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.time.LocalDateTime
import java.util.concurrent.Executors

fun main() {
    val threadPool = Executors.newFixedThreadPool(200)
    val serverSocket = ServerSocket(HOST_PORT)
    println("Server started at port $HOST_PORT. Listening for client connections...")
    try {
        while (true) {
            val socket = serverSocket.accept()
            threadPool.submit { handle(socket) }
        }
    } finally {
        threadPool.shutdown()
        serverSocket.close()
    }
}

private fun handle(socket: Socket) {
    println("${Thread.currentThread().name} Client connected: ${socket.remoteSocketAddress}")
    var clientRequest: String

    socket.use {
        BufferedReader(InputStreamReader(socket.getInputStream())).use { reader ->
            PrintWriter(socket.getOutputStream()).use { writer ->
                while (true) {
                    clientRequest = reader.readLine()
                    println("${Thread.currentThread().name} :Message from: ${socket.remoteSocketAddress}, message $clientRequest")
                    writer.println("$clientRequest server time ${LocalDateTime.now()}")
                    writer.flush()
                    println("${Thread.currentThread().name} : Response sent to ${socket.remoteSocketAddress} Message: $clientRequest")
                }
            }
        }
    }
}