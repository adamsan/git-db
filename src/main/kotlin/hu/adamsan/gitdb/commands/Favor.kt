package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import hu.adamsan.gitdb.logging.LoggingUtil
import java.util.logging.Logger


class Favor(private val repoDao: RepoDao) {
    private val log: Logger = LoggingUtil.getLogger(this.javaClass.name)

    fun favor(parameters: List<String>?) = setFavorite(parameters, true)
    fun unFavor(parameters: List<String>?) = setFavorite(parameters, false)


    private fun setFavorite(parameters: List<String>?, favour: Boolean) {
        val maybeRepo = try {
            repoDao.findById(parameters?.get(0)!!.toInt())
        } catch (ex: Exception) {
            repoDao.findByPath(System.getProperty("user.dir"))
        }
        maybeRepo.ifPresent { repo ->
            log.info("setting favorite to $favour on ${repo.name}")
            repo.favorite = favour
            repoDao.update(repo)
        }
    }
}
