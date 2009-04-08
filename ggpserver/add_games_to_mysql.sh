#!/bin/bash

# after running this script, import output.sql into the sql database

function process_single_game {
	local GAME_NAME=$(basename $1 .lisp)

	NUM_ROLES=$(grep -Eic "^[[:space:]]*\(role [^?]" $1)
	# this is of course an evil hack and by no means correct,
	# but it works for the current games

	echo -n "INSERT INTO \`ggpserver\`.\`games\` (\`name\`, \`num_roles\`, \`gamedescription\`) VALUES ('$GAME_NAME', '$NUM_ROLES', '"
	sed "s/'/''/g" $1   # replace ' by '' (MySQL string escape) 
	echo "');"
	echo ""
}



for GAME in *.lisp
do \
	process_single_game $GAME >> output.sql
done