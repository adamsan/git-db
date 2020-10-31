 git config --global init.templatedir %userprofile%/.git-templates
 mkdir %userprofile%\.git-templates\hooks

echo #!/bin/sh > %userprofile%\.git-templates\hooks\post-commit
echo # Log last 5 commits on commit >> %userprofile%\.git-templates\hooks\post-commit
echo git lg -5 >> %userprofile%\.git-templates\hooks\post-commit
echo #echo "USING GITDB..." >> %userprofile%\.git-templates\hooks\post-commit

