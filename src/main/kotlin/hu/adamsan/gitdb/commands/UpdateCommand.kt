package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This command will be invoked automatically from the git hooks after commit, to update the database:
 * gradle -p C:\Users\Adamsan\IdeaProjects\gitdb bootRun --args "update"
 */
class UpdateCommand(val userHome: String, val  repoDao: RepoDao) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun updateForId(args: List<String>?) {
        val id = args!![0].toInt()
        repoDao.findById(id).map { repo ->
            log.info("updating $repo")
            repo.commits = InitObject.countCommits(repo.path)
            repo.lastCommitted = InitObject.modifiedDateForLastCommit(repo.path)
            repo.hasRemote = InitObject.hasRemote(repo.path)
            log.info("updated: $repo")
            log.info("repo has remote: ${InitObject.hasRemote(repo.path)}" )
            repoDao.update(repo)
        }
    }
}
