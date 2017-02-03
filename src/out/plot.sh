#!/bin/bash
echo "Enter the file path:"
read file
echo "1, 2, 3, or 4?"
echo "respectively: energy, magnetisation, chi, c."
read num

a = $("magnetic")

plot="
reset;\n
set term png;\n
set xlabel \"Temperature\";
set ylabel $a;
"

echo $plot > plot.gnp
