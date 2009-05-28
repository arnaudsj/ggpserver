To build GameController run
$ ant -f my-build.xml
This will create three jar files:
- gamecontroller.jar - used by ggpserver
- gamecontroller-cli.jar - the command line version
- gamecontroller-gui.jar - a version with a simple gui

You can run the CLI and GUI versions of gamecontroller with
$ java -jar gamecontroller-???.jar

To use the stylesheets in the resources/ directory you have
to run GameController with the parameter "-printxml OUTPUTDIR XSLT"
where OUTPUTDIR is a directory in which GameController writes xml files,
one for each state of the match and XSLT is a relative or absolute URL
to some xslt stylesheet that is referenced from the state xml files.
It's a bit tricky to get the directories right, because image references
in the xslt have to be relative to the state xml. The xsl files in the 
resources directory assume that OUTPUTDIR is in the same directory as the
stylesheets directory.
E.g., the following directory structure and parameter should work:

./matches/  # a directory for the xml files
./stylesheets/ # the stylesheets from resources directory

$ java -jar gamecontroller-cli.jar SomeMatchID bidding-tictactoe.gdl 120 30 \
	-remote 2 MyPlayer localhost 4000 \
	-printxml matches/ ../../stylesheets/bidding_tictactoe/bidding_tictactoe.xsl

This should generate the following files:
./matches/SomeMatchID/step_*.xml
./matches/SomeMatchID/finalstate.xml

Just open any of the xml files in a web browser that supports xslt (the stylesheets
are known to work with Firefox 3).
If you open local files you might have to change settings in Firefox, because the URL
to the stylesheets contains "..". Type "about:config" in the address bar and change
the following setting:
security.fileuri.strict_origin_policy=false
