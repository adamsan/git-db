package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import hu.adamsan.gitdb.render.Align
import hu.adamsan.gitdb.render.Align.*
import hu.adamsan.gitdb.render.Table


class ListCommand(private val userHome: String, private val repoDao: RepoDao) {


    fun list(parameters: List<String>?) {
        if (parameters.isNullOrEmpty())
            listAll()
        else {
            println("list in $userHome by args $parameters")
            TODO("Implement list with parameters")
        }
    }

    private fun listAll() {
        val repos = repoDao.getAll()

        val table = Table()

        table.addHeader(" ID ", " NAME ", " PATH "," FAV "," COMMITS "," LAST COMMIT ", " HAS_REMOTE ")

        repos.forEach { row ->
            table.addRow(row.id,
                    row.name,
                    row.path,
                    if(row.favorite) "*" else "",
                    row.commits,
                    row.lastCommitted,
                    if(row.hasRemote) "*" else ""
            )
        }

        println(table.render(listOf(RIGHT, LEFT, LEFT, CENTER, RIGHT, LEFT, CENTER)))
    }
}
