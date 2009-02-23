# set terminal postscript eps lw 2 color
set terminal svg

# set output "results2005.eps"
set output "results2005.svg"

set title "GGP Competition 2005"
set xlabel "match set"
set ylabel "player rating"
set key below
plot 'results2005.dat' using 1:5 t "Fluxplayer" with lines, \
   '' using 1:3 t "Goblin" with lines, \
   '' using 1:4 t "Cluneplayer" with lines, \
   '' using 1:6 t "York" with lines, \
   '' using 1:7 t "Utexas" with lines, \
   '' using 1:8 t "Semsyn" with lines, \
   '' using 1:2 t "Ncsuremote" with lines
