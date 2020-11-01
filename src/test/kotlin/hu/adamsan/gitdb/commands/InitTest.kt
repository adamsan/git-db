package hu.adamsan.gitdb.commands

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal class InitTest {

    val sut = Init("", "gitdb")

    @Test
    fun assert_createGitDbDir_creates_db(@TempDir tmpHome: Path) {
        val dbDir = Paths.get(tmpHome.resolve(".git-db").toString())
        sut.createGitDbDir(tmpHome.toString())
        assertTrue(Files.isDirectory(dbDir))
    }
}