set terminal postscript eps fontfile '/usr/share/texmf/fonts/type1/public/cm-super/sfrm1000.pfb' "SFRM1000" lw 2 color
set output "direct_scores.eps"
# set title "Direct scores of all players\n(Competition 2007 Preliminaries; using round weights 0.25, 0.5, 0.5 and 1.0)"
set xlabel "Match set number"
set ylabel "Accumulated score of player"
set key below
plot "direct_scores.dat" using 1:2 t "U-TEXAS-LARG" with lines \
   , "direct_scores.dat" using 1:3 t "CLUNEPLAYER" with lines \
   , "direct_scores.dat" using 1:4 t "JIGSAWBOT" with lines \
   , "direct_scores.dat" using 1:5 t "FLUXPLAYER" with lines \
   , "direct_scores.dat" using 1:6 t "LUCKY-LEMMING" with lines \
   , "direct_scores.dat" using 1:7 t "W-WOLFE" lc 1 with lines \
   , "direct_scores.dat" using 1:8 t "CADIA-PLAYER" with lines \
   , "direct_scores.dat" using 1:9 t "ARY" with lines
