package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import hu.adamsan.gitdb.dao.RepoDao
import java.nio.file.Paths


class ChangeDirectory(val userHome: String, val repoDao: RepoDao) {
    fun cd(projectId: String?) {
        val id = projectId!!.toInt()
        val repo = repoDao.findById(id).get()
        val dir = repo.path
        println("Read DB from $userHome")
        println("cd /d $dir")
        println("testing createHook")
        createHook(repo)
    }


    private fun createHook(repo: Repo) {
        val hookPath = Paths.get(repo.path).resolve(".git").resolve("hooks").resolve("post-commit")
        if( hookPath.parent.toFile().isDirectory && !hookPath.toFile().isFile) {
            hookPath.toFile().createNewFile()
            hookPath.toFile().writeText("gitdb update ${repo.id}")
        }
    }
}