package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.DriverManager
import java.util.*
import kotlin.collections.ArrayList


class Init(var userHome: String, val appname: String, val repoDao: RepoDao) : Command {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    override fun run() {
        createGitDbDir()
        createDb()

        createHooks() //TODO

        val repos = findGitReposOnMachine()

        clearTableAndSaveRepos(repos)
        // TODO:
        // clear db and save, save in database
        // complete post-hook to update gitdb


    }

    private fun clearTableAndSaveRepos(repos: List<String>) {
        repoDao.deleteAll()
        repos.forEachIndexed { i, repo -> saveRepo(i + 1, repo) }
    }

    private fun saveRepo(ind: Int, dir: String) {
        val name = Paths.get(dir).fileName.toString()
        val lastCommitted = InitObject.modifiedDateForLastCommit(dir)
        log.info("$dir's last committed date: $lastCommitted")
        val repo = Repo(ind, name, dir, false, InitObject.countCommits(dir), lastCommitted)
        repoDao.insert(repo)
    }

    fun createGitDbDir(): Boolean {
        log.info("create .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f = File(resolve.toString())
        return f.mkdir()
    }

    fun createDb() {
        val dbPath = InitObject.dbPath(userHome)
        log.info("create DB in $dbPath")
        dbPath.toFile().createNewFile()
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { con ->
            val stmt = con.prepareStatement(InitObject.createSql)
            stmt.execute()
        }
    }

    private fun gitConfigTemplateDir() {
        // edit .gitconfig:
        // git config --global init.templatedir %userprofile%/.git-db/.git-templates
    }

    private fun createHooks() {
        // create hooks
    }

    private fun findGitReposOnMachine(): List<String> {
        val drives = FileSystems.getDefault().rootDirectories
        // TODO: uncomment after dev - commenting below line because it takes too long to search the computer
        val gitrepos = drives.flatMap { findGitReposInDrive(it) }.map { it.toString() }
        return gitrepos

//        val someGitrepos = listOf(
//                "D:\\workspaces\\web_practice\\todo",
//                "D:\\workspaces\\web_practice\\webapp-runner",
//                "E:\\flask_learn\\flask_project",
//                "E:\\tmp\\docker_doodle\\doodle",
//                "E:\\tmp\\dockert_test\\foobarX" // git repo with no commits
//        )
//        return someGitrepos
    }

    private fun findGitReposInDrive(drive: Path): List<Path> {
        log.info("Start processing drive: $drive")
        val depth = 8

        val options: Set<FileVisitOption> = setOf(FileVisitOption.FOLLOW_LINKS)
        val gitDirs: MutableList<Path> = ArrayList()

        val visitor = object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult =
                    if (File(dir!!.resolve(".git").toString()).isDirectory) {
                        log.info("Found .git in $dir")
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
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun dbPath(userHome: String): Path = Paths.get(userHome, ".git-db", ".repos.db")

    fun countCommits(dir: String): Int {
        val command = "git rev-list @ --count"
        val pb = ProcessBuilder(command.split(" "))
        pb.directory(File(dir))
        val p = pb.start()
        var count = 0
        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { count = Integer.parseInt(it) }
        }

        log.info("Number of commits in repo: $dir = $count")
        return count
    }

    fun modifiedDateForLastCommit(dir: String): Date? = unixTimestampForLastCommit(dir)?.let { Date(it*1000) }

    private fun unixTimestampForLastCommit(dir: String): Long? {
        val command = "git log -1 --format=%at"
        val pb = ProcessBuilder(command.split(" "))
        pb.directory(File(dir))
        val p = pb.start()
        var timestamp: Long? = null//0L
        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { timestamp = it.toLong() }
        }

        log.info("Unix timestamp for last commit: $dir = $timestamp")
        return timestamp
    }

    val createSql = """CREATE TABLE IF NOT EXISTS REPO(
                               ID INT PRIMARY KEY     NOT NULL,
                               NAME           TEXT    NOT NULL,
                               PATH           TEXT     NOT NULL,
                               FAVORITE       INTEGER DEFAULT 0,
                               COMMITS        INTEGER DEFAULT 1,
                               LAST_COMMITTED TEXT DEFAULT NULL
                            );""".trimIndent()
}
