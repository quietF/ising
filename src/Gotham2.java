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
		System.out.println("Glauber dynamics? (y/n)");
		String g = in.next();
		boolean glaub = false, kawas = false;
		if(g.equals("y") || g.equals("Y")){
			glaub = true;
			kawas = false;
		} else{
			System.out.println("Kawasaki dynamics? (y/n)");
			g = in.next();
			if(g.equals("y") || g.equals("Y")){
				glaub = false;
				kawas = true;
			} else System.out.println("Then what do you want!");
		}
		in.close();
		
		
		LatticeSpins Random = new LatticeSpins(N, temp);
		
		String outFile = "out/ising_allup_T" + temp + ".dat";

		Random.getData(outFile, temp, temp+5, glaub, false);
		
		/*
		 * 
		 * For T=2 the stabilization of alldown is at 100000. 
		 */
		
	}

}