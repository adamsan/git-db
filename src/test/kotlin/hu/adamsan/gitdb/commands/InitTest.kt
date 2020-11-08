package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import hu.adamsan.gitdb.dao.RepoDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mockito
import java.io.File
import java.nio.file.*

internal class InitTest {

    private val repoDao = Mockito.mock(RepoDao::class.java)

    private val sut = Init("", repoDao)

    @Test
    fun assert_createGitDbDir_creates_directory(@TempDir tmpHome: Path) {
        sut.userHome = tmpHome.toString()
        val dbDir = Paths.get(tmpHome.resolve(".git-db").toString())
        val success: Boolean = sut.createGitDbDir()
        assertTrue(success)
        assertThat(dbDir.toFile()).exists().isEmptyDirectory()
    }

    @Test
    fun assert_createGitDbConfigDb_creates_db(@TempDir tmpHome: Path) {
        sut.userHome = tmpHome.toString()
        val db = tmpHome.resolve(".git-db").resolve(".repos.db").toString()
        assertTrue(sut.createGitDbDir())
        sut.createDb()
        assertThat(File(db)).exists().isFile()
    }

    @Test
    fun assert_walks_through_filesystem() {


        val p = Paths.get("D:\\workspaces\\web_practice\\todo")
        val commits = InitObject.countCommits("D:\\workspaces\\web_practice\\todo")
        val date = InitObject.lastCommitDate("D:\\workspaces\\web_practice\\todo")
        val r = Repo(1,p.fileName.toString(), p.toString(), false, commits, date, false)
        println(r)


        // sut.processGitReposOnMachine()
    }


}