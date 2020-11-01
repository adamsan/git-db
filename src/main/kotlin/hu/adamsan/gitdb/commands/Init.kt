package hu.adamsan.gitdb.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.DriverManager


class Init(var userHome: String, val appname: String) : Command {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    override fun run() {
        createGitDbDir()
        createDb()

        //TODO:
        gitConfigTemplateDir()
        createHooks()
        processGitReposOnMachine()

    }

    fun createGitDbDir(): Boolean {
        log.info("$appname creates .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f = File(resolve.toString())
        return f.mkdir()
    }

    fun createDb() {
        val db = InitObject.dbPath(userHome)
        db.toFile().createNewFile()

        val createSql = InitObject.createSql
        DriverManager.getConnection("jdbc:sqlite:$db").use { con ->
            val stmt = con.prepareStatement(createSql)
            stmt.execute()
        }
    }

    fun gitConfigTemplateDir() {
        // edit .gitconfig:
        // git config --global init.templatedir %userprofile%/.git-db/.git-templates
    }

    fun createHooks() {
        // create hooks
    }

    fun processGitReposOnMachine() {
        val drives = FileSystems.getDefault().rootDirectories
        val gitrepos = drives.flatMap { findGitReposInDrive(it) }

        gitrepos.forEach { println(it) }
        // search hard drives for .git dirs
        // if no post-hook exists: add
        // else: complete post-hook to update gitdb
        // save in database
    }

    internal fun findGitReposInDrive(drive: Path): List<Path> {
        log.info("Start processing drive: $drive")
        val depth = 4

        val options: Set<FileVisitOption> = setOf(FileVisitOption.FOLLOW_LINKS)
        val gitDirs: MutableList<Path> = ArrayList()

        val visitor = object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if (File(dir!!.resolve(".git").toString()).isDirectory) {
                    // println(dir.toString())
                    gitDirs.add(dir)
                    return FileVisitResult.SKIP_SUBTREE
                } else {
                    return FileVisitResult.CONTINUE
                }
            }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
                return FileVisitResult.SKIP_SUBTREE
            }

        }
        Files.walkFileTree(drive, options, depth, visitor)

        return gitDirs
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
