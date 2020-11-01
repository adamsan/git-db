package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.DriverManager


class Init(var userHome: String, val appname: String) : Command {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    override fun run() {
        createGitDbDir(userHome)
        createDb(userHome)

        //TODO:
        gitConfigTemplateDir(userHome)
        createHooks(userHome)
        processGitReposOnMachine(userHome)

    }

    fun createGitDbDir(userHome: String): Boolean {
        log.info("$appname creates .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f: File = File(resolve.toString())
        return f.mkdir()
    }

    fun createDb(userHome: String) {
        val db = InitObject.dbPath(userHome)
        db.toFile().createNewFile()

//        repoDao.initRepo()

        val createSql = InitObject.createSql
        DriverManager.getConnection("jdbc:sqlite:$db").use { con ->
            val stmt = con.prepareStatement(createSql)
            stmt.execute()
        }
    }

    fun gitConfigTemplateDir(userHome: String) {
        // edit .gitconfig:
        // git config --global init.templatedir %userprofile%/.git-db/.git-templates
    }

    fun createHooks(userHome: String) {
        // create hooks
    }

    fun processGitReposOnMachine(userHome: String) {
        // search hard drives for .git dirs
        // if no post-hook exists: add
        // else: complete post-hook to update gitdb
        // save in database
    }
}

object InitObject {
    fun dbPath(userHome: String): Path = Paths.get(userHome, ".git-db", ".repos.db")

    val createSql = """CREATE TABLE IF NOT EXISTS REPO(
                               ID INT PRIMARY KEY     NOT NULL,
                               NAME           TEXT    NOT NULL,
                               PATH           TEXT     NOT NULL,
                               FAVORITE       INTEGER DEFAULT 0,
                               COMMITS        INTEGER DEFAULT 1,
                               LAST_COMMITTED TEXT
                            );""".trimIndent()
}
