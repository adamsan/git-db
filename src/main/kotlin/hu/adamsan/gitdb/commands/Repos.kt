package hu.adamsan.gitdb.commands


class Repos(val userHome: String) {
    fun list() {
        println("List.. $userHome")
    }
}