package hu.adamsan.gitdb.commands


class ChangeDirectory(userHome: String) {
    fun cd(projectId: String?) {
        if (projectId == null) {
            println("Project id can't be null")
        } else {
            println("Changing directory to $projectId")
        }
        TODO("Not yet implemented")
    }
}