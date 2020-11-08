package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao


class Favor(private val repoDao: RepoDao) {
    fun favor(parameters: List<String>?) {
        val id = parameters!![0].toInt()
        setFavorite(id, true)
    }

    fun unFavor(parameters: List<String>?) {
        val id = parameters!![0].toInt()
        setFavorite(id, false)
    }

    private fun setFavorite(id: Int, isFavorite: Boolean) {
        repoDao.findById(id).ifPresent { repo ->
            repo.favorite = isFavorite
            repoDao.update(repo)
        }
    }
}
