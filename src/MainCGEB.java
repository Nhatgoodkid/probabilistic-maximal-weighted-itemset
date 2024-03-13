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

		try {
			UncertainTransaction.loadFile(fileToPath("test.txt"), uncertainDB);
//			UncertainTransaction.loadFile(".//T40I10D100K_with_P.dat.txt", uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double minSupport = 0.4;
		double minProbability = 0.06;

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
