set terminal postscript eps fontfile '/usr/share/texmf/fonts/type1/public/cm-super/sfrm1000.pfb' "SFRM1000" lw 2 color
set output "constant_linear_regression_1_0_with_initial_values.eps"
# set title "Constant Linear Regression Ratings of all players\n(Competition 2007 Preliminaries, constant learning rate = 1.0)"
set xlabel "Match set number"
set ylabel "Player rating"
set key below
plot "constant_linear_regression_1_0_with_initial_values.dat" using 1:2 t "U-TEXAS-LARG" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:3 t "CLUNEPLAYER" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:4 t "JIGSAWBOT" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:5 t "FLUXPLAYER" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:6 t "LUCKY-LEMMING" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:7 t "W-WOLFE" lc 1 with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:8 t "CADIA-PLAYER" with lines \
   , "constant_linear_regression_1_0_with_initial_values.dat" using 1:9 t "ARY" with lines
