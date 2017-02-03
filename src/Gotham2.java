import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Gotham2 {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner in = new Scanner(System.in);
		
		System.out.println(System.getProperty("user.dir"));
		
		System.out.println("Enter the number of spins (N): ");
		int N = in.nextInt();
		System.out.println("Enter the temperature: ");
		double temp = in.nextDouble();
		System.out.println("Type glauber or kawasaki for their respective "
				+ "dynamics. Default is glauber.");
		String g = in.next();
		boolean dynamics = true;
		if(g.equalsIgnoreCase("glauber") || g.equalsIgnoreCase("g")) 
			dynamics = true;
		else if(g.equalsIgnoreCase("kawasaki") || g.equalsIgnoreCase("k")) 
			dynamics = false;
		in.close();
		
		
		LatticeSpins Random = new LatticeSpins(N, temp);
		
		String outFile = "out/ising_allup_T" + temp + ".dat";

		Random.getData(outFile, temp, temp+5, dynamics, false);
		
		/*
		 * 
		 * For T=2 the stabilization of alldown is at 100000. 
		 */
		
	}

}