package hu.adamsan.gitdb.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths


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

    fun createGitDbDir(userHome: String) {
        log.info("$appname creates .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f: File = File(resolve.toString())
        f.mkdir()
    }

}