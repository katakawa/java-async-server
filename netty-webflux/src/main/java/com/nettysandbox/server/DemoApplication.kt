package com.nettysandbox.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

/**
 * Whilst eventLoop threads do not get blocked in WebFlux, it's important to keep in mind that
 * in WebFlux the backpressure is regulated by the transport flow control (TCP) but it does not expose
 * the real demand of the recipient.
 *
 * WebFlux uses TCP flow control to regulate the backpressure in bytes.
 * But it does not handle the logical elements the consumer can receive.
 *
 * https://www.baeldung.com/spring-webflux-backpressure
 * https://stackoverflow.com/questions/52244808/backpressure-mechanism-in-spring-web-flux
 */
@RestController
class DummyController {

	@RequestMapping("/test")
	fun executedInEpollThreadNonBlocking() : Mono<String> {
		println("Current Thread is: ${Thread.currentThread().name}, class is: ${this.javaClass.name}")
		return Mono.just("Hello World").log("Stream peek: ${Thread.currentThread().name}")
	}

	@RequestMapping("/test1")
	fun executedInReactorThreadBlocking() : Mono<String> {
		println("Current Thread is: ${Thread.currentThread().name}, class is: ${this.javaClass.name}")
		return Mono.just("Hello World").delayElement(Duration.ofSeconds(1)).log("Stream peek: ${Thread.currentThread().name}")
	}

}