package hu.adamsan.gitdb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitdbApplication

fun main(args: Array<String>) {
	runApplication<GitdbApplication>(*args)
}
