package hu.adamsan.gitdb.commands

class Help {
    val name = "gitdb"
    fun help() {
        println("""
			A command line tool to better organize, find, and inspect local git repositories.
			
			Usage:			
			$name help - prints this message
			$name init - initializes the tool - scans all drives, ignores hidden directories
			$name init quick - initializes the tool by scanning the parents of the repos already stored in db
			$name list - lists all git repositories
			$name cd <id> - changes directory to repository's directory
			$name favor [id] - mark repository as favorite (repo identified by ID or by current path in the command line)
			$name unfavor [id] - unmark repository as favorite

			$name update <project_id | 0>- updates git repo on current directory - used by git hooks
		""".trimIndent())
    }

}
