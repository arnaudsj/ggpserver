#!/bin/sh

install/ggp_rating_system.sh -i data/competition2005/xml/ -o data/competition2005/output/ -c 1.0 -v -t
install/ggp_rating_system.sh -i data/competition2006/xml/ -o data/competition2006/output/ -p data/competition2005/output/constant_linear_regression_1.0.csv -c 1.0 -t
install/ggp_rating_system.sh -i data/competition2007/xml/ -o data/competition2007/output/ -p data/competition2006/output/constant_linear_regression_1.0.csv -c 1.0 -v -t
install/ggp_rating_system.sh -i data/competition2008/xml/ -o data/competition2008/output/ -p data/competition2007/output/constant_linear_regression_1.0.csv -c 1.0 -v -t
