#!/bin/bash

LEARNING_RATE=1.0

ant install

[ -d output ] || mkdir output

install/ggp_rating_system.sh -i data/competition2006/xml/ -o output/2006/ \
	-c $LEARNING_RATE -v -g
install/ggp_rating_system.sh -i data/competition2007/xml/ -o output/2007/ \
	-c $LEARNING_RATE -v -g -p output/2006/constant_linear_regression_$LEARNING_RATE.csv
install/ggp_rating_system.sh -i data/competition2008/xml/ -o output/2008/ \
	-c $LEARNING_RATE -v -g -p output/2007/constant_linear_regression_$LEARNING_RATE.csv

cp data/competition*/results*.gnuplot output/
cd output
mv 2006/constant_linear_regression_$LEARNING_RATE.dat results2006.dat
mv 2006/constant_linear_regression_$LEARNING_RATE.csv results2006.csv
mv 2007/constant_linear_regression_$LEARNING_RATE.dat results2007.dat
mv 2007/constant_linear_regression_$LEARNING_RATE.csv results2007.csv
mv 2008/constant_linear_regression_$LEARNING_RATE.dat results2008.dat
mv 2008/constant_linear_regression_$LEARNING_RATE.csv results2008.csv

for FILE in *.gnuplot
	do gnuplot < $FILE
done

rmdir 2006 2007 2008
rm *.gnuplot *.dat
