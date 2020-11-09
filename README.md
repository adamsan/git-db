# <img src="images/gitdb_logo.png" width="80" height="80" /> GITDB 

> A command line tool to better organize, find, and inspect local git repositories.

### What problem does it solve?

If you have a lot of repositories on your local machine _(work, tutorial, experiments, hobby projects, etc..)_, in different folders, drives,
it can be hard to keep track, where everything is.

GITDB tries to solve this problem by scanning the computer for existing git repositories, and keeping a record about them in a sqlite database
in `~/.gitdb/repos.db`, and hooking into git commands, and updating the state of repositories.

You can find and list all the repositories on your machine, or your most recent ones, and `cd` into it.

### Commands:
 
| Command | Description | 
| --- | --- | 
| `gitdb init` | creates database, starts scan on all drives for directories containing .git folder. Creates and configures global templatedir git config --global init.templatedir %userprofile%/.git-db/.git-templates. Adds a post-commit git hook to all repos with an update <id> call. |
| `gitdb init quick` | same as above, but it only searches in parents of existing git repos (in db), not in drives. For quick testing. | 
| `gitdb list` | lists .git repositories on your machine from it's database |
| `gitdb cd <repo_id>` | change directory to repository identified by id (number / sha / project name) |
| `gitdb favor <repo_id>` | mark repository as favorite |
| `gitdb unfavor <repo_id>` | unmark repository as favorite |
| `gitdb help` | prints help
| `gitdb update <repo_id>` | updates git repo in the current directory - used by git hooks


### Install and initialization

- #### From Release
    Java install not needed (TODO: include jre to release bundle)
    - extract zip to a folder, for example `D:\Java\gitdb`
    - set GITDB_HOME environment variable with value of the above folder, and add GITDB_HOME to PATH environment variable
    - run `gitdb init` command, and confirm. It can take a long time, (~30 min)
- #### From Source
    Required java version: Java 11
    - clone source code
    - set GITDB_HOME environment variable to an existing folder, `D:\Java\gitdb` and add GITDB_HOME to PATH environment variable
    - open up a console and run `gradlew buildAndCopyJar` - this will build the project and copy the jar, and startup scripts to GITDB_HOME
    - run `gitdb init` command, and confirm. It can take a long time, (~30 min) 


### Screenshots
 <img src="images/gitdb_list.png" width="100%" />
 <img src="images/gitdb_mix.png" width="100%" />

### Similar projects

- https://github.com/MirkoLedda/git-summary
- https://github.com/lzakharov/gitls
