package hu.adamsan.gitdb

import hu.adamsan.gitdb.commands.ChangeDirectory
import hu.adamsan.gitdb.commands.Help
import hu.adamsan.gitdb.commands.Init
import hu.adamsan.gitdb.commands.Repos
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitdbApplication : ApplicationRunner {

    @Value("\${user.home}")
    val userHome = ""

    @Value(value = "\${app.name:'gitdb'}")
    val name = ""

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(args: ApplicationArguments?) {
        val command = args?.sourceArgs?.getOrNull(0)?.toLowerCase()?.trimEnd()?.trimStart() ?: "help"

        log.info("$name was invoked with command ${command}")
        log.info("User home: $userHome")

        when (command) {
            "init" -> Init(userHome, name).run()
            "list" -> Repos(userHome).list();
            "cd" -> ChangeDirectory(userHome).cd(args?.sourceArgs?.get(1))
            else -> Help().run()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GitdbApplication>(*args)
}
