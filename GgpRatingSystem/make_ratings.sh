#!/bin/bash
#     Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>
#
#     This file is part of GgpRatingSystem.
#
#     GgpRatingSystem is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     (at your option) any later version.
#
#     GgpRatingSystem is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
#
#     You should have received a copy of the GNU General Public License
#     along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.

LEARNING_RATE=1.0

ant install

[ -d output ] || mkdir output

install/ggp_rating_system.sh -i data/competition2005/xml/ -o output/2005/ \
	-c $LEARNING_RATE -v -g -t
install/ggp_rating_system.sh -i data/competition2006/xml/ -o output/2006/ \
	-c $LEARNING_RATE -v -g -t -p output/2005/constant_linear_regression_$LEARNING_RATE.csv
install/ggp_rating_system.sh -i data/competition2007/xml/ -o output/2007/ \
	-c $LEARNING_RATE -v -g -t -p output/2006/constant_linear_regression_$LEARNING_RATE.csv
install/ggp_rating_system.sh -i data/competition2008/xml/ -o output/2008/ \
	-c $LEARNING_RATE -v -g -t -p output/2007/constant_linear_regression_$LEARNING_RATE.csv

cp data/competition*/results*.gnuplot output/
cd output

for YEAR in 200*
do \
	mv $YEAR/constant_linear_regression_$LEARNING_RATE.dat results$YEAR.dat
	mv $YEAR/constant_linear_regression_$LEARNING_RATE.csv results$YEAR.csv
	mv $YEAR/constant_linear_regression_$LEARNING_RATE.html results$YEAR.html
done

for FILE in *.gnuplot
do gnuplot < $FILE
done

for SVGFILE in *.svg
do \
	PNGFILE=$(basename $SVGFILE .svg).png
	convert $SVGFILE $PNGFILE
done

rmdir 2005 2006 2007 2008
rm *.gnuplot *.dat

