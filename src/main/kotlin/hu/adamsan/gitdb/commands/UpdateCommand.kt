package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This command will be invoked automatically from the git hooks after commit, to update the database:
 * gradle -p C:\Users\Adamsan\IdeaProjects\gitdb bootRun --args "update"
 */
class UpdateCommand(val userHome: String, val  repoDao: RepoDao) : Command {
    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun run() {
        val maybeRepo = repoDao.findByPath(getCurrentDir())
        val repo = maybeRepo!!.get()
        log.info("increment commit count for $maybeRepo")
        repo.commits += 1
        repoDao.update(maybeRepo.get())
    }

    private fun getCurrentDir(): String {
        val dir = System.getProperty("user.dir")
        log.info("Current working directory: $dir")
        return dir
    }
}
