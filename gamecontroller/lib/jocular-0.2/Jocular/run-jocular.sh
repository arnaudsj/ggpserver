#!/bin/bash


# FIXME: this should be a better test for Windows here.
if [ "$OS" = "Windows_NT" ]; then
    CLASSPATH="bin;../Common/bin"
else
    CLASSPATH="bin:../Common/bin"
fi

java -cp $CLASSPATH stanfordlogic.jocular.Main $@
