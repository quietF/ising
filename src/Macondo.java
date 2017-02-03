import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Macondo {
	
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
		boolean dynamics = true; // true means glauber.
		if(g.equalsIgnoreCase("glauber")) dynamics = true;
		else if(g.equalsIgnoreCase("kawasaki")) dynamics = false;
		in.close();
		
		
		LatticeSpins Random = new LatticeSpins(N, temp);
		
		Random.dynamical(100000000, dynamics, dynamics);
		
		/*
		 * 
		 * For T=2 the stabilization of alldown is at 100000. 
		 */
		
	}

}