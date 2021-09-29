package com.networktest.client

import com.networktest.HOST_NAME
import com.networktest.HOST_PORT
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.time.LocalDateTime

fun main() {
    var response: String
    var message: String

    Socket(HOST_NAME, HOST_PORT).use { socket ->
        BufferedReader(InputStreamReader(socket.getInputStream())).use { serverResponseReader ->
            PrintWriter(socket.getOutputStream()).use { clientPrintWriter ->
                while (true) {
                    println("Type message to send:")
                    message = "${readLine()!!} time: ${LocalDateTime.now()}"
                    clientPrintWriter.println(message)
                    clientPrintWriter.flush()
                    println("Sent: $message")

                    response = serverResponseReader.readLine()
                    println("Received: $response")
                }
            }
        }
    }

}
