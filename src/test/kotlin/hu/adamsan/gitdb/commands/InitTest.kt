package hu.adamsan.gitdb.commands

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

internal class InitTest {

    private val sut = Init("", "gitdb")

    @Test
    fun assert_createGitDbDir_creates_directory(@TempDir tmpHome: Path) {
        val dbDir = Paths.get(tmpHome.resolve(".git-db").toString())
        val success: Boolean = sut.createGitDbDir(tmpHome.toString())
        assertTrue(success)
        assertThat(dbDir.toFile()).exists().isEmptyDirectory()
    }

    @Test
    fun assert_createGitDbConfigDb_creates_db(@TempDir tmpHome: Path) {
        val db = tmpHome.resolve(".git-db").resolve(".repos.db").toString()
        assertTrue(sut.createGitDbDir(tmpHome.toString()))
        sut.createDb(tmpHome.toString())
        assertThat(File(db)).exists().isFile()
    }
}