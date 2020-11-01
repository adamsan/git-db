package hu.adamsan.gitdb.models

import java.util.*

data class Repo(
        val id: Int,
        val name: String,
        val path: String,
        var favorite: Boolean,
        var commits: Int,
        var lastCommitted: Date
)