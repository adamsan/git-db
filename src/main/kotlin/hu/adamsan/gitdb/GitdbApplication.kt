package hu.adamsan.gitdb

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitdbApplication : ApplicationRunner {

    val name = "gitdb"

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(args: ApplicationArguments?) {

        val command = args?.sourceArgs?.getOrNull(0)?.toLowerCase()?.trimEnd()?.trimStart() ?: "help"

        log.info("Command = ${command}")

        when (command) {
            "init" -> init()
            "list" -> list();
            "cd" -> cd(args?.sourceArgs?.get(1))
            else -> help()
        }
    }

    private fun list() {
        TODO("Not yet implemented")
    }

    private fun init() {
        TODO("Not yet implemented")
    }

    private fun cd(projectId: String?) {
        if (projectId == null) {
            println("Project id can't be null")
        } else {
            println("Changing directory to ${projectId}")
        }
        TODO("Not yet implemented")
    }

    private fun help() {
        println("""
			A command line tool to better organize, find, and inspect local git repositories.
			
			Usage:			
			${name} help - prints this message
			${name} init - initializes the tool
			${name} list - lists all git repositories
			${name} cd <project_id> - changes directory to repository's directory
		""".trimIndent())
    }

}

fun main(args: Array<String>) {
    runApplication<GitdbApplication>(*args)
}
