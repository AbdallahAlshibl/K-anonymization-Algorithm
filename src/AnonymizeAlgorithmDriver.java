import java.io.*;
import java.util.*;

public class AnonymizeAlgorithmDriver {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int k, attribute;

		// the value of k in anonymized table ...
		System.out.println("Please enter the value of k ...");
		k = sc.nextInt();

		// how many attributes in the table ...
		System.out.println("Please enter the number of attributes ...");
		attribute = sc.nextInt();
		
		sc.close();

		File file = new File("data.txt");

		Anonymize_Algorithm obj = new Anonymize_Algorithm(k,attribute, file);
		obj.createAnonymizedTable();
	}

}
