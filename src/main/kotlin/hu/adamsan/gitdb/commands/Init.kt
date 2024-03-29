package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.DriverManager
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList


class Init(var userHome: String, private val repoDao: RepoDao) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    private val configDir = ".git-db"

    fun init(parameters: List<String>?) {
        if (!confirmRun()) {
            return
        }
        createGitDbDir()
        createGitConfigInitTemplateDir()
        val repoDirs = if (parameters.isNullOrEmpty() || parameters[0].trim() == "") {
            deleteAndCreateDb()
            findGitReposOnMachine()
        } else if (parameters[0].trim() == "quick") {
            findGitReposOnMachineByExistingDbRoots()
        } else {
            throw RuntimeException("Unknown parameter: ${parameters[0]}")
        }

        val repos = clearTableAndSaveRepos(repoDirs)
        repos.forEach { createHook(it) }
    }

    private fun createGitConfigInitTemplateDir() {
        // git config --global init.templatedir %userprofile%/.git-templates
        val gitTemplatePath = "$userHome/.git-db/.git-templates"
        val command = "git config --global init.templatedir $gitTemplatePath"
        println("Running command: \n\t$command")
        ProcessBuilder(command.split(" ")).start()

        val hooks = Paths.get(gitTemplatePath, "hooks").toFile()
        if (!hooks.exists())
            hooks.mkdirs()
        writePostCommitHooks(gitTemplatePath)
    }

    private fun writePostCommitHooks(gitDir: String, id: Int = 0) {
        writeCommand(gitDir, "post-commit", "#!/bin/sh\ngitdb update $id\n")
        writeCommand(gitDir, "post-commit.bat", "gitdb update $id\n")
    }

    private fun writeCommand(gitDir: String, hookFile: String, command: String) {
        val postCommit = Paths.get(gitDir, "hooks", hookFile).toFile()
        postCommit.writeText(command)
        postCommit.setExecutable(true)
    }

    private fun confirmRun(): Boolean {
        if (InitObject.dbPath(userHome).toFile().exists()) {
            println("Database already exists. This will recreate it. Are you sure you want to continue? (Y/N)")
        } else {
            println("This will initialize database, but it can take long (~30 min). Are you sure you want to continue? (Y/N)")
        }
        return readLine()!!.uppercase().startsWith("Y")
    }

    private fun clearTableAndSaveRepos(repoPaths: List<String>): List<Repo> {
        repoDao.deleteAll()
        val repos = repoPaths.mapIndexed { i, path -> repoFromPath(i + 1, path) }
        repos.forEach { repoDao.insert(it) }
        return repos
    }

    private fun repoFromPath(id: Int, dir: String): Repo {
        val name = Paths.get(dir).fileName.toString()
        val lastCommitted = InitObject.lastCommitDate(dir)
        return Repo(id, name, dir, false, InitObject.countCommits(dir), lastCommitted, InitObject.hasRemote(dir))
    }

    fun createGitDbDir(): Boolean {
        println("create .git-db dir in user home directory: $userHome")
        val userHomePath = Paths.get(userHome)
        val resolve = userHomePath.resolve(configDir)
        val f = File(resolve.toString())
        return f.mkdir()
    }

    fun deleteAndCreateDb() {
        val dbPath = InitObject.dbPath(userHome)
        println("create DB in $dbPath")
        dbPath.toFile().delete()
        dbPath.toFile().createNewFile()
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { con ->
            val stmt = con.prepareStatement(InitObject.createSql)
            stmt.execute()
        }
    }

    private fun createHook(repo: Repo) {
        writePostCommitHooks(Paths.get(repo.path, ".git").toAbsolutePath().toString(), repo.id)
    }

    private fun findGitReposOnMachineByExistingDbRoots(): List<String> {
        val workspaces = repoDao.getAll().stream().map { Paths.get(it.path).parent }.collect(Collectors.toSet())
        return workspaces.flatMap { findGitReposIn(it) }.map { it.toString() }.distinct()
    }

    private fun findGitReposOnMachine(): List<String> {
        val drives = FileSystems.getDefault().rootDirectories
        return drives.flatMap { findGitReposIn(it) }.map { it.toString() }
    }

    private fun findGitReposIn(path: Path): List<Path> {
        val maxDepth = 8
        val depth = maxDepth - path.nameCount
        println("Start processing: $path\nSearch max-depth: $depth")
        var visited = 0

        val options: Set<FileVisitOption> = setOf(FileVisitOption.FOLLOW_LINKS)
        val gitDirs: MutableList<Path> = ArrayList()

        val visitor = object : SimpleFileVisitor<Path>() {
            override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                visited += 1
                return when {
                    File(dir!!.resolve(".git").toString()).isDirectory -> {
                        log.info("Found .git in $dir")
                        gitDirs.add(dir)
                        FileVisitResult.SKIP_SUBTREE
                    }
                    isProcDir(dir) || isDotHiddenDir(dir) || isRecycleBin(dir) -> {
                        FileVisitResult.SKIP_SUBTREE
                    }
                    else -> {
                        val latestFoundMessage = if (gitDirs.isEmpty()) "" else " | Latest repo found: ${gitDirs.get(gitDirs.size - 1).toString()}"
                        print("Found .git repositories: ${gitDirs.size} | Visited directories: $visited $latestFoundMessage\r")
                        FileVisitResult.CONTINUE
                    }
                }
            }

            private fun isProcDir(dir: Path): Boolean = dir.toString().startsWith("/proc")

            private fun isRecycleBin(dir: Path): Boolean {
                return dir.toFile().isDirectory && "\$RECYCLE.BIN" == dir.fileName?.toString()
            }

            // .vim/ .cookiecutters/ .jenkins/
            private fun isDotHiddenDir(dir: Path): Boolean {
                return dir.toFile().isDirectory && dir.fileName?.toString()?.startsWith(".") ?: false
            }

            override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult = FileVisitResult.SKIP_SUBTREE

        }
        Files.walkFileTree(path, options, depth, visitor)
        println()
        return gitDirs
    }
}

object InitObject {
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

        return count
    }

    fun lastCommitDate(dir: String): Date? = unixTimestampForLastCommit(dir)?.let { Date(it * 1000) }

    fun hasRemote(dir: String): Boolean {
        val command = "git remote -v"
        val p = ProcessBuilder(command.split(" ")).directory(File(dir)).start()
        p.inputStream.bufferedReader().useLines { lines ->
            return lines.any { it.contains("origin") }
        }
    }

    private fun unixTimestampForLastCommit(dir: String): Long? {
        val command = "git log -1 --format=%at"
        val pb = ProcessBuilder(command.split(" "))
        pb.directory(File(dir))
        val p = pb.start()
        var timestamp: Long? = null//0L
        p.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { timestamp = it.toLong() }
        }
        return timestamp
    }

    val createSql = """CREATE TABLE IF NOT EXISTS REPO(
                               ID INT PRIMARY KEY     NOT NULL,
                               NAME           TEXT    NOT NULL,
                               PATH           TEXT     NOT NULL UNIQUE,
                               FAVORITE       INTEGER DEFAULT 0,
                               COMMITS        INTEGER DEFAULT 1,
                               LAST_COMMITTED TEXT DEFAULT NULL,
                               HAS_REMOTE     INTEGER DEFAULT 0
                            );""".trimIndent()
}
