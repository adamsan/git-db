package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * This command will be invoked automatically from the git hooks after commit, to update the database:
 * gradle -p C:\Users\Adamsan\IdeaProjects\gitdb bootRun --args "update"
 */
class UpdateCommand(val userHome: String, val repoDao: RepoDao) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun updateForId(args: List<String>?) {
        val id = args!![0].toInt()
        if (id == 0) {
            insertNewRepo()
            return
        }
        repoDao.findById(id).map { repo ->
            log.info("updating $repo")
            repo.commits = InitObject.countCommits(repo.path)
            repo.lastCommitted = InitObject.modifiedDateForLastCommit(repo.path)
            repo.hasRemote = InitObject.hasRemote(repo.path)
            log.info("updated: $repo")
            repoDao.update(repo)
        }
    }

    private fun insertNewRepo() {
        log.info("update called with 0, trying to insert repo")
        val dir = System.getProperty("user.dir")
        log.info("Current working directory: $dir")
        log.info(".git dir exists:" + Paths.get(dir, ".git").toFile().exists())
        val id = insertOrUpdateRepoInDb(dir)
        updatePostCommitHook(id, dir)
    }

    private fun updatePostCommitHook(id: Int, dir: String?) {
        println(id)
        // update post-commit gitdb update call
        val hooksPath = Paths.get(dir, ".git", "hooks")
        listOf("post-commit", "post-commit.bat").forEach {
            val postCommitHookPath = hooksPath.resolve(it)
            val content = postCommitHookPath.toFile().readLines()
            val updateCommand = "gitdb update $id"
            var changedContent = content.stream()
                    .map { line -> if (line.contains("gitdb update 0")) updateCommand else line }
                    .collect(Collectors.joining("\n"))
            if (!changedContent.contains(updateCommand)) {
                changedContent += "\n" + updateCommand
            }
            postCommitHookPath.toFile().writeText(changedContent)
        }
    }

    private fun insertOrUpdateRepoInDb(dir: String): Int {
        val lastCommitted = InitObject.modifiedDateForLastCommit(dir)
        val commits = InitObject.countCommits(dir)
        val hasRemote = InitObject.hasRemote(dir)
        val name = Paths.get(dir).fileName.toString()

        val maybeRepo = repoDao.findByPath(dir)

        return if (maybeRepo?.isEmpty == true) {
            val newRepo = Repo(repoDao.getUnusedIndex(), name, dir, false, commits, lastCommitted, hasRemote)
            repoDao.insert(newRepo)
            newRepo.id
        } else {
            val repo = maybeRepo!!.get()
            repo.commits = commits
            repo.lastCommitted = lastCommitted
            repo.hasRemote = hasRemote
            repoDao.update(repo)
            repo.id
        }
    }
}
