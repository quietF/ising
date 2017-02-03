import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class LatticeSpins {
	
	private int N = 10;
	private int n = 1000;
	private int[][] spins = new int[N][N];
	private double T = 1;
	private double kB = 1.;
	private double J = 1.;
	private final BufferedImage bi;
	private int[] indexPlus = new int[N];
	private int[] indexMinus = new int[N];
	
	//private final Object lock = new Object();
	private final Frame f = new Frame();
	
	public LatticeSpins(int spinsN, double temp) {
		if(spinsN > 0 && temp > 0){
			this.N = spinsN;
			this.T = temp;
			this.spins = new int[this.N][this.N];
			this.indexPlus = new int[this.N];
			this.indexMinus = new int[this.N];
			for(int i=0; i<this.N; i++){
				/*
				 * these arrays are used to deal with the periodicity 
				 * of the spin lattice.
				 */
				this.indexPlus[i] = i+1;
				this.indexMinus[i] = i-1;
			}
			this.indexMinus[0] = this.N-1;
			this.indexPlus[this.N-1] = 0;

			this.bi = new BufferedImage(this.N, this.N, 
					BufferedImage.TYPE_INT_RGB);
			
		} else throw new IllegalArgumentException("Neither number of spins nor"
				+ "temperature can be zero.");
	}
	
	public void startDown(){
		/*
		 * Initialise spins with S_i = -1 for all.
		 */
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++)
				this.spins[i][j] = -1;
	}
	
	public void startRandom(){
		/*
		 * Initialise spins randomly.
		 */
		double prob;
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++){
				prob = this.getRand();
				if(prob > 0) this.spins[i][j] = 1;
				else if(prob < 0) this.spins[i][j] = -1;
				else System.out.println("Error.");
			}
	}
	
	public void init() {
		/*
		 * This generates the window with the initial configuration
		 * of spins.
		 */
		this.f.setIgnoreRepaint(true);
		this.f.setTitle("MonteCarlo with Metropolis");
		this.f.setVisible(true);
		this.f.setSize(this.N, this.N + this.f.getInsets().top);
		//this.f.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.f.addWindowListener(new WindowAdapter() 
		{public void windowClosing(WindowEvent we) {System.exit(0);}});
		
		
		for (int i = 0; i < this.bi.getWidth(); i++) 
			for (int j = 0; j < this.bi.getHeight(); j++) 
				this.bi.setRGB(i, j, this.spins[i][j] == 1 ? Color.BLACK.getRGB()
						: Color.WHITE.getRGB());
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}

	public void update() {
		/*
		 * Replot the spin arrangement
		 */
		for(int i=0; i<this.bi.getWidth(); i++)
			for(int j=0; j<this.bi.getHeight(); j++)
				this.bi.setRGB(i, j, this.spins[i][j] == 1 ? Color.BLACK.getRGB()
						: Color.WHITE.getRGB());
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}
	
	public int getNumIterations(){
		return this.n;
	}
	
	public void setNumIterations(int numIterations){
		this.n = numIterations;
	}
	
	public double energyij(int i, int j){
		/*
		 * returns the energy of one spin's interaction
		 * with its four neighbours.
		 */
		return - this.J * this.spins[i][j] * (
				this.spins[indexPlus[i]][j] + 
				this.spins[indexMinus[i]][j] +
				this.spins[i][indexPlus[j]] +
				this.spins[i][indexMinus[j]]);
	}
	
	public double energy1neigh(int[] ij1, int[] ij2){
		/*
		 * get the energy of the interaction between two
		 * neighbouring spins.
		 */
		return - this.J * this.spins[ij1[0]][ij1[1]] * 
				this.spins[ij2[0]][ij2[1]];
	}
	
	public double energyTotal(){
		/*
		 * returns the total energy of the spin lattice.
		 */
		double totE = 0;
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++)
				totE += this.energyij(i, j);
		return totE / 2.;
	}
	
	public double magnetisationTotal(){
		/*
		 * returns the magnetisation of the spin lattice.
		 */
		double mag = 0;
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++)
				mag += this.spins[i][j];
		return mag;
	}
	
	public double getRand(){
		/*
		 * returns random number btw -1 and 1.
		 */
		double rand = 1 - 2 * Math.random();
		if(rand == 0) rand = this.getRand();
		return rand;
	}
	
	public double expBeta(double dE){
		/*
		 * returns thermodynamic beta
		 */
		return Math.exp(- dE / (this.T * this.kB));
	}
	
	public int[] getRandSite(){
		/*
		 * returns an int[] that determines a random site
		 */
		int[] rand = {(int) (this.N * Math.random()), (int) (this.N * Math.random())};
		return rand;
	}

	public void flipSpin(int i, int j){
		/*
		 * changes the value of a spin in position (i,j)
		 */
		if(this.spins[i][j] == 1) this.spins[i][j] = -1;
		else if(this.spins[i][j] == -1) this.spins[i][j] = 1;
		else System.out.println("Error. spins[i][j] should be +- 1.");
	}
	
	public void swapSpin(int[] ij1, int[] ij2){
		/*
		 * swaps two spins in the specified positions.
		 */
		if(ij1[0] == ij2[0] && ij1[1] == ij2[1]) this.swapSpin(ij1, ij2);
		else{
			int aux = this.spins[ij1[0]][ij1[1]];
			this.spins[ij1[0]][ij1[1]] = this.spins[ij2[0]][ij2[1]];
			this.spins[ij2[0]][ij2[1]] = aux;
		}
	}
	
	public boolean metropolisGlauber(int i, int j){
		/*
		 * run glauber dynamics
		 */
		double initialE = this.energyij(i, j);
		this.flipSpin(i, j);
		double finalE = this.energyij(i, j), dE = finalE - initialE;
		if(dE < 0) return true;
		else if(Math.random() <= this.expBeta(dE)) return true;
		else{
			this.flipSpin(i, j);
			return false;
		}
	}
	
	public boolean metropolisKawasaki(int[] ij1, int[]ij2){
		/*
		 * run kawasaki dynamics
		 */
		if(ij1[0] != ij2[0] && ij1[1] != ij2[1]){
			if(ij1[0] == ij2[0] || ij1[1] == ij2[1]){
				// Condition is fulfilled when the spins that want
				// to be changed are neighbours. If they are
				// their interaction energy is counted two times.
				// a factor this.energy1neigh(ij1, ij2) is substracted
				// from the initial and final energy calculations.
				
				double initialE = this.energyij(ij1[0], ij1[1]) + 
						this.energyij(ij2[0], ij2[1]) - 
						this.energy1neigh(ij1, ij2);
				this.swapSpin(ij1, ij2);
				double finalE = this.energyij(ij1[0], ij1[1]) + 
						this.energyij(ij2[0], ij2[1]) -
						this.energy1neigh(ij1, ij2);
				double dE = finalE - initialE;
				System.out.println(dE);
				if(dE < 0) return true;
				else if(Math.random() <= this.expBeta(dE)) return true;
				else{
					this.swapSpin(ij1, ij2);
					return false;
				}
			} else{
				double initialE = this.energyij(ij1[0], ij1[1]) + 
						this.energyij(ij2[0], ij2[1]);
				this.swapSpin(ij1, ij2);
				double finalE = this.energyij(ij1[0], ij1[1]) + 
						this.energyij(ij2[0], ij2[1]);
				double dE = finalE - initialE;
				if(dE < 0) return true;
				else if(Math.random() <= this.expBeta(dE)) return true;
				else{
					this.swapSpin(ij1, ij2);
					return false;
				}
			}
		} else{
			ij1 = this.getRandSite();
			ij2 = this.getRandSite();
			return this.metropolisKawasaki(ij1, ij2);
		}
	}
	
	public void dynamical(int numIterations, 
			boolean glauber, boolean visual) 
					throws FileNotFoundException, UnsupportedEncodingException{
		/*
		 * loops over numIterations with the desired dynamics
		 * writes E and M into the out/M_vs_E.dat file.
		 */
		this.startRandom();
		int numFrames = 1000;
		this.setNumIterations(numIterations);
		if(visual)
			this.init();
		PrintWriter writer;
		writer = new PrintWriter("out/M_vs_E.dat", "UTF-8");
		int count = 0;
		double energyTotal = this.energyTotal();
		for(int i=0; i<this.getNumIterations(); i++){
			int[] randSite = this.getRandSite();
			if(glauber){
				this.metropolisGlauber(randSite[0], randSite[1]);
				if(i % (this.getNumIterations() / numFrames) == 0){
					if(visual)
						this.update();
					energyTotal = this.energyTotal();
					writer.println(count + " " + energyTotal + " " + 
							this.magnetisationTotal());
					count += 1;
				}
			}else{
				int[] randSite2 = this.getRandSite();
				this.metropolisKawasaki(randSite, randSite2);
				if(i % (this.getNumIterations() / numFrames) == 0){
					this.update();
					energyTotal = this.energyTotal();
					writer.println(count + " " + energyTotal + " " + 
							this.magnetisationTotal());	
					count += 1;
				}
			}
		}
		writer.close();
	}
	
	public void dynamical(PrintWriter writer, int numIterations, boolean glauber){
		/*
		 * get normalised magnetisation and energy
		 * to normalise magnetisation: divide by N^2
		 * to normalise energy: divide by 2JN^2 (the 
		 * energy of an all up/down set of N by N spins
		 * is E_max = - 2 * J * N^2
		 */
		for(int i=0; i<numIterations; i++){
			int[] randSite = this.getRandSite();
			if(glauber){
				this.metropolisGlauber(randSite[0], randSite[1]);
				writer.println(i + " " + this.energyTotal() / (2 * this.J * this.N * 
						this.N) + " " + Math.abs(this.magnetisationTotal()) / 
						(this.N * this.N));
			}else{
				int[] randSite2 = this.getRandSite();
				this.metropolisKawasaki(randSite, randSite2);
				writer.println(i + " " + this.energyTotal() + " " + 
							Math.abs(this.magnetisationTotal()));	
			}
		}
	}
	
	public void dynamicalVoid(int numIterations, boolean glauber){
		 /* get normalised magnetisation and energy
		 * to normalise magnetisation: divide by N^2
		 * to normalise energy: divide by 2JN^2 (the 
		 * energy of an all up/down set of N by N spins
		 * is E_max = - 2 * J * N^2
		 */
		for(int i=0; i<numIterations; i++){
			int[] randSite = this.getRandSite();
			if(glauber){
				this.metropolisGlauber(randSite[0], randSite[1]);
			}else{
				int[] randSite2 = this.getRandSite();
				this.metropolisKawasaki(randSite, randSite2);	
			}
		}
	}
	
	public double getFluctuations(double avgEorM, double avgE2orM2, boolean energy){
		/*
		 * input is avgEorM = <E> or <M> and avgE2orM2 = <E^2> or <M^2>
		 * boolean energy is true to get specific heat per spin (c=C/N)
		 * and false to get the magnetic susceptibility (chi).
		 */
		double c_or_chi = (avgE2orM2 - avgEorM*avgEorM) / (this.N*this.kB*this.T);
		if(energy) return c_or_chi / this.T;
		else return c_or_chi;
	}
	
	public double[] getJacknife(double[] E, double[] M, double sumE, double sumE2, 
			double sumM, double sumM2){
		/*
		 * returns magnetic susceptibility with its error
		 * and specific heat / N with its error (in that order).
		 */
		int n = E.length;
		double[] chi_sd_c_sd = new double[4];
		chi_sd_c_sd[0] = this.getFluctuations(sumM/n, sumM2/n, false);
		chi_sd_c_sd[2] = this.getFluctuations(sumE/n, sumE2/n, true);
		double chi_i = 0., c_i = 0.;
		for(int i=0; i<n; i++){
			chi_i = this.getFluctuations((sumM-M[i])/(double)(n-1), 
					(sumM2-M[i]*M[i])/(double)(n-1), false);
			c_i = this.getFluctuations((sumE-E[i])/(double)(n-1), 
					(sumE2-E[i]*E[i])/(double)(n-1), true);
			chi_sd_c_sd[1] += (chi_i-chi_sd_c_sd[0])*(chi_i-chi_sd_c_sd[0]);
			chi_sd_c_sd[3] += (c_i-chi_sd_c_sd[2])*(c_i-chi_sd_c_sd[2]);
		}
		chi_sd_c_sd[1] = Math.sqrt(chi_sd_c_sd[1]);
		chi_sd_c_sd[3] = Math.sqrt(chi_sd_c_sd[3]);
		return chi_sd_c_sd;
	}
	
	public double[] getJacknifeKawasaki(double[] E, double sumE, double sumE2){
		/*
		 * returns specific spin / N with its error (in that order)
		 */
		int n = E.length;
		double[] c_sd = new double[2];
		c_sd[0] = this.getFluctuations(sumE/n, sumE2/n, true);
		double c_i = 0.;
		for(int i=0; i<n; i++){
			c_i = this.getFluctuations((sumE-E[i])/(double)(n-1), 
					(sumE2-E[i]*E[i])/(double)(n-1), true);
			c_sd[1] += (c_i-c_sd[0])*(c_i-c_sd[0]);
		}
		c_sd[1] = Math.sqrt(c_sd[1]);
		return c_sd;
	}
	
	public double[] dynamical(int numIterations, boolean glauber){
		/*
		 * returns specific heat / N and magnetic susceptibility
		 * c = C/N = (<E^2> - <E>^2) / (k T^2)	as in (23)/N in MVP01.pdf
		 * chi = (<M^2> - <M>^2) / (N k T)		as in (22) in MVP01.pdf
		 * 
		 * if glauber = true: return chi, chi_error, c, c_error
		 * if kakasaki: return c, c_error;
		 */
		double[] E = new double[numIterations], M = new double[numIterations];
		double sumE = 0., sumM = 0., sumE2 = 0., sumM2 = 0.;
		for(int i=0; i<numIterations; i++){
			E[i] = this.energyTotal();
			M[i] = Math.abs(this.magnetisationTotal());
			sumE += E[i];
			sumE2 += E[i]*E[i];
			sumM += M[i];
			sumM2 += M[i]*M[i];
			if(glauber) this.dynamicalVoid(100, glauber);
			else this.dynamicalVoid(100, glauber);
		}
		if(glauber) return this.getJacknife(E, M, sumE, sumE2, sumM, sumM2);
		else return this.getJacknifeKawasaki(E, sumE, sumE2);
	}
	
	public void getData(String datFile, double minT, double maxT, int nIterations, 
			boolean glauber, boolean rand){
		/*
		 * glauber == false means kawasaki (makes no sense).
		 * if kawasaki dynamics are implemented, specific heat is
		 * generated, but magnetic susceptibility is not.
		 * rand == true means start with random spins.
		 * 
		 * The results generated will be inaccurate if rand == true
		 * for a small value of temperature, or if rand == false
		 * for a large value of temperature.
		 */
		PrintWriter writer;
		try {
			int dataPoints = 50;
			double N2 = this.N * this.N;
			writer = new PrintWriter(datFile, "UTF-8");
			if(rand || !glauber) this.startRandom(); // Starting with all down in kawasaki will remain static.
			else this.startDown();
			this.T = minT;
			this.dynamicalVoid(50000, glauber);
			if(glauber){
				double[] chi_sig_c_sig = new double[4];
				double chi_max = 0., c_max = 0., chi_aux = 0., c_aux = 0.;
				double Tc_chi = 0., Tc_c = 0.;
				for(int i=0; i<dataPoints; i++){
					if(rand) this.T = maxT - i * (maxT - minT)/dataPoints;
					else this.T= minT + i * (maxT - minT)/dataPoints;
					this.dynamicalVoid(10000, glauber);
					chi_sig_c_sig = this.dynamical(nIterations, glauber);
					System.out.println(i);
					chi_aux = chi_sig_c_sig[0];
					if(chi_aux > chi_max){
						chi_max = chi_aux;
						Tc_chi = this.T;
					}
					c_aux = chi_sig_c_sig[2];
					if(c_aux > c_max){
						c_max = c_aux;
						Tc_c = this.T;
					}
					writer.println(this.T + " " + 							// 1. Temperature
							this.energyTotal()/(2*N2*this.J) + " " + 		// 2. Normalised energy
							Math.abs(this.magnetisationTotal())/N2 + 		// 3. Normalised magnetisation
							" " + chi_sig_c_sig[0] + " " + 					// 4. Magnetic susceptibility (chi)
							chi_sig_c_sig[1] + " " + 						// 5. Error in chi 
							chi_sig_c_sig[2] + " " +						// 6. Specific heat (c = Cv / N)
							chi_sig_c_sig[3]);								// 7. Error in c
				}
				writer.close();
				System.out.println("Tc = " + Tc_chi + " using chi. Tc = " + Tc_c + " using c.");
			} else{
				double[] c_sig = new double[2];
				double c_max = 0., c_aux = 0., Tc_c = 0.;
				for(int i=0; i<dataPoints; i++){
					if(rand) this.T = maxT - i * (maxT - minT)/dataPoints;
					else this.T= minT + i * (maxT - minT)/dataPoints;
					this.dynamicalVoid(10000, glauber);
					c_sig = this.dynamical(nIterations, glauber);
					System.out.println(i);
					c_aux = c_sig[0];
					if(c_aux > c_max){
						c_max = c_aux;
						Tc_c = this.T;
					}
					System.out.println(this.energyTotal());
					writer.println(this.T + " " + 							// 1. Temperature
							this.energyTotal()/(2*N2*this.J) +				// 2. Normalised energy
							" " + Math.abs(this.magnetisationTotal())/N2 +	// 3. Normalised magnetisation
							" " + 0 + " " + 								// 4. Magnetic susceptibility (chi)
							0 + " " + 										// 5. Error in chi
							c_sig[0] + " " +								// 6. Specific heat (c = Cv / N)
							c_sig[1]);										// 7. Error in c
				}
				writer.close();
				System.out.println("Tc = " + Tc_c + " using c.");
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}