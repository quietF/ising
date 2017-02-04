#!/bin/bash
echo "Enter the file path:"
read file
# echo "1, 2, 3, or 4?"
# echo "respectively: energy, magnetisation, chi, c."
# read num
echo "number of spins?"
read N
echo "glauber or kawasaki?"
read dynamics

observable[1]='Energy'
observable[2]='Magnetisation'
observable[3]='Magnetic Susceptibility'
observable[4]='Specific heat (Cv/N)'

plotnum[1]="p '${file}' u 1:2 w l lw 2 lc rgb \"red\" t \"\""
plotnum[2]="p '${file}' u 1:3 w l lw 2 lc rgb \"dark-green\"t \"\""
plotnum[3]="p[][0:1.1] '${file}' u 1:4:5 w errorbars t \"\";\n
rep '${file}' u 1:4 pt 6 lc rgb 'black' t \"\""
plotnum[4]="p[][0:1.1] '${file}' u 1:6:7 w errorbars t \"\";\n
rep '${file}' u 1:6 pt 6 lc rgb 'blue' t \"\""

out[1]="energy_N${N}"
out[2]="magnetisation_N${N}"
out[3]="chi_N${N}"
out[4]="specific_heat_N${N}"

for i in `seq 1 4`;
do
    plot="
    reset;\n
    set title 'N=${N}';\n
    set xlabel 'Temperature';\n
    set ylabel '${observable[$i]}';\n
    ${plotnum[$i]};\n
    set term postscript eps enhanced color font \",17\";\n
    set out 'img/${out[$i]}${dynamics}.eps';\n
    replot;\n
    "

    echo -e $plot > gnp/${out[$i]}.gnp

    gnuplot gnp/${out[$i]}.gnp
    convert -density 200 img/${out[$i]}${dynamics}.eps img/${out[$i]}${dynamics}.png
    rm img/${out[$i]}${dynamics}.eps
done


