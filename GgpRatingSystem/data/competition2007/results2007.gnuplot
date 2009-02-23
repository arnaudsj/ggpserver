# set terminal postscript eps lw 2 color
set terminal svg

# set output "results2007.eps"
set output "results2007.svg"

set title "GGP Competition 2007"
set xlabel "match set"
set ylabel "player rating"
set key below
plot 'results2007.dat' using 1:7 t "Cluneplayer" with lines, \
   '' using 1:8 t "Fluxplayer" with lines, \
   '' using 1:3 t "Luckylemming" with lines, \
   '' using 1:9 t "Jigsawbot" with lines, \
   '' using 1:2 t "U-Texas-Larg" with lines, \
   '' using 1:5 t "Cadiaplayer" with lines, \
   '' using 1:6 t "Ary" with lines, \
   '' using 1:4 t "W-Wolfe" with lines, \
   '' using 1:10 t "The-Pirate" with lines
