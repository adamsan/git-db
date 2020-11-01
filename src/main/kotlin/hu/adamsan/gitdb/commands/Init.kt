package hu.adamsan.gitdb.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.sql.DriverManager


class Init(var userHome: String, val appname: String) : Command {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    override fun run() {
        createGitDbDir(userHome)

        // create sql database in user home
        // ~/.git-db/repos.db

        // edit .gitconfig:
        // git config --global init.templatedir %userprofile%/.git-db/.git-templates
        // create hooks


        // search hard drives for .git dirs
        // if no post-hook exists: add
        // else: complete post-hook to update gitdb
        // save in database
    }

    fun createGitDbDir(userHome: String): Boolean {
        log.info("$appname creates .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f: File = File(resolve.toString())
        return f.mkdir()
    }

    fun createDb(userHome: String) {
        val db = Paths.get(userHome, ".git-db", ".repos.db")
        db.toFile().createNewFile()
        val createSql = """CREATE TABLE REPO(
                               ID INT PRIMARY KEY     NOT NULL,
                               NAME           TEXT    NOT NULL,
                               PATH           TEXT     NOT NULL,
                               FAVORITE       INTEGER DEFAULT 0,
                               COMMITS        INTEGER DEFAULT 1,
                               LAST_COMMITTED TEXT
                            );""".trimIndent()
        DriverManager.getConnection("jdbc:sqlite:$db").use { con ->
            con.prepareStatement(createSql)
        }
    }
}