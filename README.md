# ising

Ising simulation of 2D spin lattice. 
Dynamics of the system are MonteCarlo based
A Metropolis algorithm for either Glauber dynamics
or Kawasaki dynamics can be chosen.

Boltzmann's constant is set to unity.
The energy of one spin (i) is: E_ij = -J sum<i,j> S_i S_j
whith J also set to unity.

There are two programs: Gotham2 and Macondo.

Gotham2 has a visual interface that shows spins flipping
for a user input number of spins per side, temperature, 
and type of dynamics (Glauber or Kawasaki). It shows
100 frames of the evolution of the system from a completely
disordered state to the real state.

Macondo produces data on Energy, Magnetisation, magnetic
susceptibility with jacknife error, and specific heat with
jacknife error. Saved to folder out/
