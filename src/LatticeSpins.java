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
				this.indexPlus[i] = i+1;
				this.indexMinus[i] = i-1;
			}
			this.indexMinus[0] = this.N-1;
			this.indexPlus[this.N-1] = 0;
			double prob;

			for(int i=0; i<this.N; i++)
				for(int j=0; j<this.N; j++){
					prob = this.getRand();
					if(prob > 0) this.spins[i][j] = 1;
					else if(prob < 0) this.spins[i][j] = -1;
					else System.out.println("Error.");
				}
			
			this.bi = new BufferedImage(this.N, this.N, 
					BufferedImage.TYPE_INT_RGB);
			
			this.f.setIgnoreRepaint(true);
			this.f.setTitle("MonteCarlo with Metropolis");
			this.f.setVisible(true);
			this.f.setSize(this.N, this.N + this.f.getInsets().top);
			//this.f.setExtendedState(Frame.MAXIMIZED_BOTH);
			this.f.addWindowListener(new WindowAdapter() 
			{public void windowClosing(WindowEvent we) {System.exit(0);}});
			
			this.init();
			
		} else throw new IllegalArgumentException("Neither number of spins nor"
				+ "temperature can be zero.");
	}
	
	public void init() {
		for (int i = 0; i < this.bi.getWidth(); i++) 
			for (int j = 0; j < this.bi.getHeight(); j++) 
				this.bi.setRGB(i, j, this.spins[i][j] == 1 ? Color.BLACK.getRGB()
						: Color.WHITE.getRGB());
		this.f.getGraphics().drawImage(this.bi, 0, this.f.getInsets().top, 
				this.f.getWidth(), this.f.getHeight()-f.getInsets().top, null);
	}

	public void update() {
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
		double up = this.spins[this.indexMinus[i]][j];
		double down = this.spins[this.indexPlus[i]][j];
		double right = this.spins[i][this.indexPlus[j]];
		double left = this.spins[i][this.indexMinus[j]];
		return - this.J * this.spins[i][j] * (up + left + down + right);

	}
	
	public double energy1neigh(int[] ij1, int[] ij2){
		return - this.J * this.spins[ij1[0]][ij1[1]] * 
				this.spins[ij2[0]][ij2[1]];
	}
	
	public double energyTotal(){
		/*
		 * returns the total energy of the system.
		 */
		double totE = 0;
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++)
				totE += this.energyij(i, j);
		return totE / 2.;
	}
	
	public double getMagnetisation(){
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
	
	/*public void displaySpins(){
		for(int i=0; i<this.N; i++){
			for(int j=0; j<this.N; j++)
				System.out.printf("%d ", this.spins[i][j]);
			System.out.println();
		}
	}*/
	
	public double avgSpins(){
		double avg = 0;
		for(int i=0; i<this.N; i++)
			for(int j=0; j<this.N; j++)
				avg += this.spins[i][j];
		return avg / (this.N * this.N);
	}

	public void flipSpin(int i, int j){
		if(this.spins[i][j] == 1) this.spins[i][j] = -1;
		else if(this.spins[i][j] == -1) this.spins[i][j] = 1;
		else System.out.println("Error. spins[i][j] should be +- 1.");
	}
	
	public void swapSpin(int[] ij1, int[] ij2){
		if(ij1[0] == ij2[0] && ij1[1] == ij2[1]) this.swapSpin(ij1, ij2);
		else{
			int aux = this.spins[ij1[0]][ij1[1]];
			this.spins[ij1[0]][ij1[1]] = this.spins[ij2[0]][ij2[1]];
			this.spins[ij2[0]][ij2[1]] = aux;
		}
	}
	
	public boolean metropolis(int i, int j){
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
	
	public boolean metropolisKawa(int[] ij1, int[]ij2){
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
			return this.metropolisKawa(ij1, ij2);
		}
	}
	
	public void dynamical(int numIterations, 
			boolean glauber, boolean kawasaki) throws FileNotFoundException, UnsupportedEncodingException{
		/*
		 * loops over numIterations with the desired dynamics
		 * writes E and M into the out/M_vs_E.dat file.
		 */
		if(glauber && kawasaki){
			System.out.println("Only one type of dynamics at a time.");
		}else if(glauber || kawasaki){
			int numFrames = 100;
			this.setNumIterations(numIterations);
			this.init();
			PrintWriter writer;
			writer = new PrintWriter("out/M_vs_E.dat", "UTF-8");
			int count = 0;
			for(int i=0; i<this.getNumIterations(); i++){
				int[] randSite = this.getRandSite();
				if(glauber){
					this.metropolis(randSite[0], randSite[1]);
					if(i % (this.getNumIterations() / numFrames) == 0){
						this.update();
						writer.println(count + " " + this.energyTotal() + " " + 
								this.getMagnetisation());
						count += 1;
					}
				}else if(kawasaki){
					int[] randSite2 = this.getRandSite();
					this.metropolisKawa(randSite, randSite2);
					if(i % (this.getNumIterations() / numFrames) == 0){
						this.update();
						writer.println(count + " " + this.energyTotal() + " " + 
								this.getMagnetisation());	
						count += 1;
					}
				}
			}
			writer.close();
		}else System.out.println("Both dynamics cannot be false.");
	}
}