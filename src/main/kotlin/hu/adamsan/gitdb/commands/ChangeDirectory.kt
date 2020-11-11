package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import java.nio.file.Paths


class ChangeDirectory(private val userHome: String, private val repoDao: RepoDao, private val gitDbHome: String) {

    fun cd(projectId: String?) {
        val id = projectId!!.trim().toInt()
        val dir = repoDao.findById(id).get().path

        Paths.get(gitDbHome, "run_cd.bat").toFile().writeText("cd /d \"$dir\"")
        Paths.get(gitDbHome, "run_cd").toFile().writeText("cd \"$dir\"")
    }
}
