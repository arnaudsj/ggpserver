set terminal postscript eps lw 2 color
set output "results2008.eps"
set title "GGP Competition 2008"
set xlabel "match set"
set ylabel "player rating"
set key below
plot 'results2008.dat' using 1:5 t "Cluneplayer" with lines, \
   '' using 1:6 t "Fluxplayer" with lines, \
   '' using 1:3 t "Maligne" with lines, \
   '' using 1:7 t "Monomaniac" with lines, \
   '' using 1:9 t "Largplayer" with lines, \
   '' using 1:2 t "Cadiaplayer" with lines, \
   '' using 1:4 t "Ary" with lines, \
   '' using 1:10 t "Centurio" with lines, \
   '' using 1:8 t "Testplayer" with lines
