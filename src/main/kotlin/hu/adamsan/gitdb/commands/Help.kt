package hu.adamsan.gitdb.commands

class Help : Command {
    val name = "gitdb"
    override fun run() {
        println("""
			A command line tool to better organize, find, and inspect local git repositories.
			
			Usage:			
			$name help - prints this message
			$name init - initializes the tool - scans all drives, ignores hidden directories
            $name init quick - initializes the tool by scanning the parents of the repos already stored in db
			$name list - lists all git repositories
			$name cd <project_id> - changes directory to repository's directory

            $name update <project_id | 0>- updates git repo on current directory - used by git hooks
		""".trimIndent())
    }

}
