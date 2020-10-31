package hu.adamsan.gitdb.commands


class Init: Command {

    override fun run() {
        // create sql database in user home
        // ~/.git-db/repos.db

        // edit .gitconfig:
        // git config --global init.templatedir %userprofile%/.git-db/.git-templates
        // create hooks


        // search hard drives for .git dirs
        // if no post-hook exists: add
        // else: complete post-hook to update gitdb
        // save in database
    }

}