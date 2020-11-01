package hu.adamsan.gitdb.models

import java.util.*

data class Repo(val id: Int, val name: String, var favorite: Boolean, var commits:Int, var lastCommitted:Date)