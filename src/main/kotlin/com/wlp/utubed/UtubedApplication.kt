package com.wlp.utubed

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UtubedApplication

fun main(args: Array<String>) {
	runApplication<UtubedApplication>(*args)
}
