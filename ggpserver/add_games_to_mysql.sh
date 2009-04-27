#!/bin/bash

# after running this script, import output.sql into the sql database

function process_single_game {
	local GAME_NAME=$(basename $1 .lisp)

	echo "game: $1"

	echo -n "INSERT INTO \`ggpserver\`.\`games\` (\`name\`, \`gamedescription\`) VALUES ('$GAME_NAME', '"
	sed "s/'/''/g" $1   # replace ' by '' (MySQL string escape) 
	echo "');"
	echo ""
}



for GAME in *.lisp
do \
	process_single_game $GAME >> output.sql
done

