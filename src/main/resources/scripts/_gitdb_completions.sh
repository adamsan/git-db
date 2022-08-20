#!/bin/bash

#source this file in bashrc to get autocompletion working

_gitdb_completions() {
	if [ $COMP_CWORD -le 1 ];
	then
		COMPREPLY+=($( compgen -W "list ls init favor unfavor cd help" "${COMP_WORDS[1]}"));
	elif [ $COMP_CWORD -le 2 ];
	then
		if [ "${COMP_WORDS[1]}" == "init" ];
		then
			COMPREPLY+=($( compgen -W "quick" "${COMP_WORDS[2]}"));
		elif [ "${COMP_WORDS[1]}" == "list" ];
		then
			COMPREPLY+=($( compgen -W "dir" "${COMP_WORDS[2]}"));
		fi;
	fi;
}

complete -F _gitdb_completions gitdb

# . $GITDB_HOME/_gitdb_completions.sh

