package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.Repo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.SimpleFileVisitor
import java.util.*

internal class InitTest {

    private val sut = Init("", "gitdb")

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
        val commits = sut.countCommits("D:\\workspaces\\web_practice\\todo")
        val ts = sut.unixTimestampForLastCommit("D:\\workspaces\\web_practice\\todo")
        val date = Date(ts)

        val r = Repo(1,p.fileName.toString(), p.toString(), false, commits, date )
        println(r)


        // sut.processGitReposOnMachine()
    }


}