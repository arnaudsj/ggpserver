set terminal postscript eps fontfile '/usr/share/texmf/fonts/type1/public/cm-super/sfrm1000.pfb' "SFRM1000" lw 2color
set output "dynamic_linear_regression_60.eps"
# set title "Dynamic linear regression ratings of all players\n(Competition 2007 Preliminaries, dynamic learning rate = 6.0...1.6)"
set xlabel "Match set number"
set ylabel "Player rating"
set key below
plot "dynamic_linear_regression_60.dat" using 1:2 t "U-TEXAS-LARG" with lines \
   , "dynamic_linear_regression_60.dat" using 1:3 t "CLUNEPLAYER" with lines \
   , "dynamic_linear_regression_60.dat" using 1:4 t "JIGSAWBOT" with lines \
   , "dynamic_linear_regression_60.dat" using 1:5 t "FLUXPLAYER" with lines \
   , "dynamic_linear_regression_60.dat" using 1:6 t "LUCKY-LEMMING" with lines \
   , "dynamic_linear_regression_60.dat" using 1:7 t "W-WOLFE" lc 1 with lines \
   , "dynamic_linear_regression_60.dat" using 1:8 t "CADIA-PLAYER" with lines \
   , "dynamic_linear_regression_60.dat" using 1:9 t "ARY" with lines
