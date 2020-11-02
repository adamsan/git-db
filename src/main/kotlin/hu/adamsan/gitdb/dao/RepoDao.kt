package hu.adamsan.gitdb.dao

import hu.adamsan.gitdb.commands.InitObject
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
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

    fun getAll(): List<Repo> {
        return jdbi.withHandle<List<Repo>, Exception> { h ->
            h.select("SELECT * FROM REPO")
                    .map(mapper)
                    .list()
        }
    }

    fun findById(id: Int): Optional<Repo> {
        val sql = "SELECT * FROM REPO WHERE id=:id"
        return jdbi.withHandle<Optional<Repo>, Exception> { h ->
            h.createQuery(sql)
                    .bind("id", id)
                    .map(mapper)
                    .findFirst()
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

    fun update(repo: Repo) {
        val sql = "UPDATE REPO " +
                "SET name=:name, path=:path, favorite=:favorite, commits=:commits, last_committed= :lastCommitted " +
                "WHERE id= :id"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
                    .bindBean(repo)
                    .execute()
        }
    }

    fun delete(repoId: Int) {
        var sql = "DELETE FROM REPO WHERE id=:id"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
                    .bind("id", repoId)
                    .execute()
        }
    }

    fun deleteAll() {
        var sql = "DELETE FROM REPO"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
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