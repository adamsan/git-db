package hu.adamsan.gitdb

import hu.adamsan.gitdb.commands.*
import hu.adamsan.gitdb.dao.RepoDao
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlite3.SQLitePlugin
import org.slf4j.LoggerFactory

class GitdbApplication {

    private val userHome: String = System.getProperty("user.home")
    private val gitDbHome: String = System.getProperty("GITDB_HOME") ?: System.getenv("GITDB_HOME")
    private val log = LoggerFactory.getLogger(this.javaClass)

    private fun jdbi(): Jdbi {
        val db = InitObject.dbPath(userHome.orEmpty()).toString()
        val jdbi = Jdbi.create("jdbc:sqlite:$db")
        jdbi.installPlugin(SQLitePlugin())
        return jdbi
    }

    fun run(args: List<String>) {
        val command = if (args.isNotEmpty()) args.get(0).toLowerCase().trimEnd().trimStart() else "help"
        println("running gitdb command: $command")
        log.info("User home: $userHome")

        var repoDao = RepoDao(jdbi());

        when (command) {
            "init" -> Init(userHome, repoDao).init(args.drop(1))
            "list" -> ListCommand(userHome, repoDao).list(args.drop(1));
            "cd" -> ChangeDirectory(userHome, repoDao, gitDbHome).cd(args.get(1)) // cd should be implemented in bat files?
            "update" -> UpdateCommand(userHome, repoDao).updateForId(args.drop(1))
            "favor" -> Favor(repoDao).favor(args.drop(1))
            "unfavor" -> Favor(repoDao).unFavor(args.drop(1))
            else -> Help().help()
        }
    }
}

fun main(args: Array<String>) {
    setLogLevelPropertyFromEnv()
    GitdbApplication().run(args.toList())
}

private fun setLogLevelPropertyFromEnv() {
    try {
        val key = "org.slf4j.simpleLogger.defaultLogLevel"
        System.getenv(key)?.let { System.setProperty(key, it) }
    } catch (ex: SecurityException) {
    }
}