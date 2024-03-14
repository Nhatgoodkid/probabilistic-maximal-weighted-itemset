import algorithms.CGEB;
import pattern.itemset.UncertainTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainCGEB {
	public static void main(String[] args) {
		List<UncertainTransaction<Integer>> uncertainDB = new ArrayList<>();
		String inputPath = "dataset/test.txt";
//		String inputPath = "dataset/T40I10D100K_with_P.dat.txt";
		try {
			UncertainTransaction.loadFile(fileToPath(inputPath), uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(UncertainTransaction<Integer> transaction : uncertainDB) {
			System.out.println(transaction.weight);
		}
		double minSupport = 0.01;
		double minProbability = 0.6;

		CGEB cgeb = new CGEB(uncertainDB, minSupport, minProbability);
		Set<Set<?>> candidates = cgeb.generateCandidates();
		System.out.println("Candidates:");
		for (Set<?> candidate : candidates) {
			System.out.println(candidate);
		}
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = Main.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
