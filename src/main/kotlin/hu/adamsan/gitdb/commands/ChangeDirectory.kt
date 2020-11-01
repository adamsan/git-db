package hu.adamsan.gitdb.commands


class ChangeDirectory(val userHome: String) {
    fun cd(projectId: String?) {
        if (projectId == null) {
            println("Project id can't be null")
        } else {
            println("Read DB from $userHome")
            println("Changing directory to $projectId")
        }
        TODO("Not yet implemented")
    }
}