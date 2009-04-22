#!/bin/bash

GameControllerStylesheetsDIR=../gamecontroller/resources/stylesheets/
GGPServerStylesheetsDIR=WebContent/stylesheets/

for s_dir in ../gamecontroller/resources/stylesheets/*
do
	s=$(basename ${s_dir})
	case ${s_dir} in
		*/generic|*/CVS)
			echo "skipping \"${s}\" ..."
		;;
		*)
			echo "copying files for \"${s}\" ..."
			( cd ${GameControllerStylesheetsDIR} ; tar --exclude=CVS -cf - ${s} ) | (cd ${GGPServerStylesheetsDIR} ; tar -xf - )
			find ${GGPServerStylesheetsDIR}/${s} -iname '*.xsl' -print0 | xargs -0 sed -i -e 's,../../stylesheets/,../stylesheets/,g'
		;;
	esac
done
