#!/bin/bash

GameControllerStylesheetsDIR=../gamecontroller/resources/stylesheets/
GGPServerStylesheetsDIR=WebContent/stylesheets/

( cd ${GameControllerStylesheetsDIR} ; tar --exclude=CVS --exclude=sitespecific.xsl -cf - * ) | (cd ${GGPServerStylesheetsDIR} ; tar -xvf - )
