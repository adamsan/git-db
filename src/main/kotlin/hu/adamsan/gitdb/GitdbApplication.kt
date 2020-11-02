package hu.adamsan.gitdb

import hu.adamsan.gitdb.commands.*
import hu.adamsan.gitdb.dao.RepoDao
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlite3.SQLitePlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GitdbApplication : ApplicationRunner {

    @Autowired
    val context: ApplicationContext = AnnotationConfigApplicationContext().apply { }

    @Value("\${user.home}")
    val userHome = ""

    @Value(value = "\${app.name:'gitdb'}")
    val name = ""

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun jdbi(): Jdbi {
        val db = InitObject.dbPath(userHome).toString()
        val jdbi = Jdbi.create("jdbc:sqlite:$db")
        jdbi.installPlugin(SQLitePlugin())
        return jdbi
    }

    @Bean
    fun help(): Help {
        return Help()
    }

    override fun run(args: ApplicationArguments?) {
        val command = args?.sourceArgs?.getOrNull(0)?.toLowerCase()?.trimEnd()?.trimStart() ?: "help"

        log.info("$name was invoked with command ${command}")
        log.info("User home: $userHome")

        //val jdbiBean = this.context.getBean(Jdbi::class.java)
        //log.info("JDBI BEAN: $jdbiBean")
        var repoDao = context.getBean(RepoDao::class.java)

        var help = context.getBean(Help::class.java)


        when (command) {
            "init" -> Init(userHome, name, repoDao).run()
            "list" -> Repos(userHome).list();
            "cd" -> ChangeDirectory(userHome).cd(args?.sourceArgs?.get(1))
            else -> help.run()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<GitdbApplication>(*args)
}
