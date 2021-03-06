package hu.adamsan.gitdb.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

data class Repo(
        val id: Int,
        val name: String,
        val path: String,
        var favorite: Boolean,
        var commits: Int,
        var lastCommitted: Date?,
        var hasRemote: Boolean = false
)

class RepoDao(val jdbi: Jdbi) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: RowMapper<Repo> = RowMapper { rs, _ ->
        Repo(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getString("PATH"),
                rs.getBoolean("FAVORITE"),
                rs.getInt("COMMITS"),
                if (rs.getLong("LAST_COMMITTED") != 0L)
                    Date(rs.getLong("LAST_COMMITTED"))
                else
                    null,
                rs.getBoolean("HAS_REMOTE")
        )
    }

    fun getUnusedIndex(): Int {
        return 1 + jdbi.withHandle<Int, Exception> { h ->
            h.select("SELECT MAX(ID) FROM REPO")
                    .mapTo(Int::class.java)
                    .one()
        }
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
        log.info("insert $repo to db")
        val sql = "INSERT INTO REPO VALUES (:id, :name, :path, :favorite, :commits, :lastCommitted, :hasRemote)"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
                    .bindBean(repo)
                    .execute()
        }
    }

    fun update(repo: Repo) {
        val sql = "UPDATE REPO " +
                "SET name=:name, path=:path, favorite=:favorite, commits=:commits, last_committed= :lastCommitted, has_remote=:hasRemote " +
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
        log.info("deleting all records from REPO table")
        var sql = "DELETE FROM REPO"
        jdbi.withHandle<Int, Exception> { h ->
            h.createUpdate(sql)
                    .execute()
        }
    }

    fun findByPath(path: String): Optional<Repo> {
        val sql = "SELECT * FROM REPO WHERE path=:path"
        return jdbi.withHandle<Optional<Repo>, Exception> { h ->
            h.createQuery(sql)
                    .bind("path", path)
                    .map(mapper)
                    .findFirst()
        }
    }
}
