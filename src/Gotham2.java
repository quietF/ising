import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Gotham2 {
	
	public static boolean readDynamics(Scanner in){
		System.out.println("Type glauber or kawasaki for their respective "
				+ "dynamics. Default is glauber.");
		boolean dynamics = true;
		String g = in.next();
		if(g.equalsIgnoreCase("glauber") || g.equalsIgnoreCase("g")) 
			dynamics = true;
		else if(g.equalsIgnoreCase("kawasaki") || g.equalsIgnoreCase("k")) 
			dynamics = false;
		return dynamics;
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner in = new Scanner(System.in);
		
		System.out.println(System.getProperty("user.dir"));
		
		System.out.println("Enter the number of spins (N): ");
		int N = in.nextInt();
		System.out.println("Enter the temperature: ");
		double temp = in.nextDouble();
		boolean dynamics = readDynamics(in);
		in.close();
		
		
		LatticeSpins Random = new LatticeSpins(N, temp);
		
		//Random.startRandom();
		//Random.dynamical(100000000, dynamics, true);
		
		String outFile = "out/ising_alldown_N"+ N +"_T" + temp + ".dat";
		int n = 10000;
		Random.getData(outFile, temp, 6, n, dynamics, false);
		
	}

}