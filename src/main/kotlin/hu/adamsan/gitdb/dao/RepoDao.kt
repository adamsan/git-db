package hu.adamsan.gitdb.dao

import hu.adamsan.gitdb.commands.InitObject
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.Update
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RepoDao(val jdbi: Jdbi) {
    private val mapper: RowMapper<Repo> = RowMapper { rs, _ ->
        Repo(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getString("PATH"),
                rs.getBoolean("FAVORITE"),
                rs.getInt("COMMITS"),
                Date(rs.getLong("LAST_COMMITTED"))
        )
    }


    fun jdbi(userHome: String): Jdbi {
        val db = InitObject.dbPath(userHome).toString()
        val jdbi = Jdbi.create("jdbc:sqlite:$db")
        jdbi.installPlugin(SqlObjectPlugin())
        return jdbi
    }

    fun initRepo() {
        val withHandle = jdbi.withHandle<Update, Exception> { h ->
            h.createUpdate(InitObject.createSql)
        }
    }

    fun getAll(): List<Repo> {
        return jdbi.withHandle<List<Repo>, java.lang.Exception> { h ->
            h.select("SELECT * FROM REPO")
                    .map(mapper)
                    .list()
        }
    }

    fun insert(repo: Repo) {
        val sql = "INSERT INTO REPO VALUES (:id, :name, :path, :favorite, :commits, :lastCommitted)"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
                    .bindBean(repo)
                    .execute()
        }
    }
}

//CREATE TABLE IF NOT EXISTS REPO(
//                               ID INT PRIMARY KEY     NOT NULL,
//                               NAME           TEXT    NOT NULL,
//                               PATH           TEXT     NOT NULL,
//                               FAVORITE       INTEGER DEFAULT 0,
//                               COMMITS        INTEGER DEFAULT 1,
//                               LAST_COMMITTED TEXT
data class Repo(
        val id: Int,
        val name: String,
        val path: String,
        var favorite: Boolean,
        var commits: Int,
        var lastCommitted: Date
)