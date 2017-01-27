import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Gotham2 {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner in = new Scanner(System.in);
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
		int n = 100000000;
		in.close();
		
		
		LatticeSpins Random = new LatticeSpins(N, temp);
		Random.dynamical(n, glaub, kawas);
		
	}

}