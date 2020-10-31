package hu.adamsan.gitdb.commands

class Help : Command {
    val name = "gitdb"
    override fun run() {
        println("""
			A command line tool to better organize, find, and inspect local git repositories.
			
			Usage:			
			$name help - prints this message
			$name init - initializes the tool
			$name list - lists all git repositories
			$name cd <project_id> - changes directory to repository's directory
		""".trimIndent())
    }

}
