package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.DriverManager


class Init(var userHome: String, val appname: String, val repoDao: RepoDao) : Command {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    override fun run() {
        createGitDbDir()
        createDb()

        gitConfigTemplateDir() //TODO
        createHooks() //TODO

        val repos = findGitReposOnMachine()

        // TODO:
        // clear db and save, save in database
        // if no post-hook exists: add
        // else: complete post-hook to update gitdb


    }

    fun countCommits(dir: String): Int {
        val command = "git rev-list @ --count"
        val pb = ProcessBuilder(command.split(" "))
        pb.directory(File(dir))
        val p = pb.start()
        var count = 0
        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { count = Integer.parseInt(it) }
        }

        println("Number of commits in repo: $dir = $count")
        return count
    }

    fun unixTimestampForLastCommit(dir: String): Long {
        val command = "git log -1 --format=%at"
        val pb = ProcessBuilder(command.split(" "))
        pb.directory(File(dir))
        val p = pb.start()
        var timestamp = 0L
        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { timestamp = it.toLong() }
        }

        println("Unix timestamp for last commit: $dir = $timestamp")
        return timestamp
    }

    fun createGitDbDir(): Boolean {
        log.info("$appname creates .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f = File(resolve.toString())
        return f.mkdir()
    }

    fun createDb() {
        val dbPath = InitObject.dbPath(userHome)
        log.info("Creating DB in $dbPath")
        dbPath.toFile().createNewFile()
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { con ->
            val stmt = con.prepareStatement(InitObject.createSql)
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

    fun findGitReposOnMachine(): List<String> {
        val drives = FileSystems.getDefault().rootDirectories
        // TODO: uncomment after dev - commenting below line because it takes too long to search the computer
        //val gitrepos = drives.flatMap { findGitReposInDrive(it) }
        // return gitrepos

        val someGitrepos = listOf("D:\\workspaces\\web_practice\\todo", "D:\\workspaces\\web_practice\\webapp-runner", "E:\\flask_learn\\flask_project", "E:\\tmp\\docker_doodle\\doodle")
        return someGitrepos
    }

    internal fun findGitReposInDrive(drive: Path): List<Path> {
        log.info("Start processing drive: $drive")
        val depth = 4

        val options: Set<FileVisitOption> = setOf(FileVisitOption.FOLLOW_LINKS)
        val gitDirs: MutableList<Path> = ArrayList()

        val visitor = object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult =
                    if (File(dir!!.resolve(".git").toString()).isDirectory) {
                        gitDirs.add(dir)
                        FileVisitResult.SKIP_SUBTREE
                    } else {
                        FileVisitResult.CONTINUE
                    }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult = FileVisitResult.SKIP_SUBTREE

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
