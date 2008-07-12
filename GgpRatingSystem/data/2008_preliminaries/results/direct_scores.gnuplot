set terminal postscript eps fontfile '/usr/share/texmf/fonts/type1/public/cm-super/sfrm1000.pfb' "SFRM1000" lw 2 color
set output "direct_scores.eps"
set title "Direct scores of all players\n(Competition 2008 Preliminaries; using round weights 0.25, 0.5, 0.5 and 1.0)"
set xlabel "Match set number"
set ylabel "Accumulated score of player"
set key below
plot "direct_scores.dat" using 1:2 t "Maligne" with lines \
   , "direct_scores.dat" using 1:3 t "Cluneplayer" with lines \
   , "direct_scores.dat" using 1:4 t "Fluxplayer" with lines \
   , "direct_scores.dat" using 1:5 t "Cadiaplayer" with lines \
   , "direct_scores.dat" using 1:6 t "Ary" with lines \
   , "direct_scores.dat" using 1:7 t "Monomaniac" lc 1 with lines \
   , "direct_scores.dat" using 1:9 t "Testplayer" with lines \
   , "direct_scores.dat" using 1:10 t "Largplayer" with lines \
   , "direct_scores.dat" using 1:11 t "Centurio" with lines
