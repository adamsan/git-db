package hu.adamsan.gitdb.dao

import hu.adamsan.gitdb.commands.InitObject
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.Update
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.springframework.stereotype.Repository
import java.lang.Exception

@Repository
class RepoDao(val jdbi: Jdbi) {
    fun initRepo() {
        val withHandle = jdbi.withHandle<Update, Exception> { h ->
            h.createUpdate(InitObject.createSql)
        }

    }
}