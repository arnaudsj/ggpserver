set terminal postscript eps lw 2 color
set output "results2006.eps"
set title "GGP Competition 2006"
set xlabel "match set"
set ylabel "player rating"
set key below
plot 'results2006.dat' using 1:9 t "Cluneplayer" with lines, \
   '' using 1:10 t "Fluxplayer" with lines, \
   '' using 1:6 t "Luckylemming" with lines, \
   '' using 1:12 t "Jigsawbot" with lines, \
   '' using 1:5 t "Ogre" with lines, \
   '' using 1:4 t "Nnrg.Hazel" with lines, \
   '' using 1:2 t "Apple-Rt" with lines, \
   '' using 1:3 t "Aidriven.Learner" with lines, \
   '' using 1:7 t "Pires5600" with lines, \
   '' using 1:8 t "The" with lines, \
   '' using 1:11 t "Ggp_Remote_Agent" with lines, \
   '' using 1:13 t "Entropy" with lines
