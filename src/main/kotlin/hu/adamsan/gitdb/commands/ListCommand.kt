package hu.adamsan.gitdb.commands

import hu.adamsan.gitdb.dao.RepoDao
import hu.adamsan.gitdb.render.Table


class ListCommand(val userHome: String, val repoDao: RepoDao) {


    fun list(parameters: List<String>?) {
        if (parameters.isNullOrEmpty())
            listAll()
        else {
            println("list in $userHome by args $parameters")
        }
    }

    private fun listAll() {
        val repos = repoDao.getAll()

        val table = Table()

        table.addHeader(" ID ", " NAME ", " PATH "," FAV "," COMMITS "," LAST COMMIT ")

        repos.forEach { row ->
            table.addRow(row.id.toString(),
                    row.name,
                    row.path,
                    if(row.favorite) "Y" else "N",
                    row.commits.toString(),
                    row.lastCommitted.toString()
            )
        }

        println(table.render())
    }
}