import algorithms.WCGEB;
import pattern.itemset.UncertainTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainWCGEB {
	public static void main(String[] args) throws IOException {
		List<UncertainTransaction<Integer>> uncertainDB = new ArrayList<>();
		String inputPath = "dataset/test.txt";
//		String inputPath = "dataset/T40I10D100K_with_P_W_10%.dat.txt";
		try {
			UncertainTransaction.loadFile(fileToPath(inputPath), uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}

		double minSupport = 0.06;
		double minProbability = 0.6;

		WCGEB wcgeb = new WCGEB(uncertainDB, minSupport, minProbability);
		Set<Set<?>> candidates = wcgeb.generateCandidates();
		wcgeb.printStats();
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
