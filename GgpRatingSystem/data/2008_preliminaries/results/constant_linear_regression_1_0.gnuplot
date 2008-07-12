set terminal postscript eps fontfile '/usr/share/texmf/fonts/type1/public/cm-super/sfrm1000.pfb' "SFRM1000" lw 2 color
set output "constant_linear_regression_1_0.eps"
set title "Constant Linear Regression Ratings of all players\n(Competition 2008 Preliminaries, constant learning rate = 1.0)"
set xlabel "Match set number"
set ylabel "Player rating"
set key below
plot "constant_linear_regression_1_0.dat" using 1:2 t "Maligne" with lines \
   , "constant_linear_regression_1_0.dat" using 1:3 t "Cluneplayer" with lines \
   , "constant_linear_regression_1_0.dat" using 1:4 t "Fluxplayer" with lines \
   , "constant_linear_regression_1_0.dat" using 1:5 t "Cadiaplayer" with lines \
   , "constant_linear_regression_1_0.dat" using 1:6 t "Ary" with lines \
   , "constant_linear_regression_1_0.dat" using 1:7 t "Monomaniac" lc 1 with lines \
   , "constant_linear_regression_1_0.dat" using 1:8 t "Testplayer" with lines \
   , "constant_linear_regression_1_0.dat" using 1:9 t "Largplayer" with lines \
   , "constant_linear_regression_1_0.dat" using 1:10 t "Centurio" with lines
