import algorithms.APFI_MAX;
import pattern.itemset.UncertainTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainAPFI_MAX_40per {

	public static void main(String[] args) throws IOException {
		// Load the uncertain database
		List<UncertainTransaction<Integer>> uncertainDB = new ArrayList<>();

//		String inputPath = "dataset/test.txt";
		String inputPath = "dataset/T10I4D100K/T10I4D100K_with_P_W_40%.dat.txt";
		try {
			UncertainTransaction.loadFile(fileToPath(inputPath), uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the minimum support and minimum probability
		double minSupport = 0.02;
		double minProbability = 0.6;

		// Create an instance of algorithms.APFI_MAX
		APFI_MAX<Integer> apfiMax = new APFI_MAX<>(uncertainDB, minSupport, minProbability);

		// Run the algorithms.APFI_MAX algorithm
		apfiMax.runAPFI_MAX("pmfi_T10I4D100K_output_40%.txt");
		// Print the PMFIs
		System.out.println("PMFIs:");
		for (Set<Integer> pmfi : apfiMax.PMFIs) {
			System.out.println(pmfi);
		}
		apfiMax.printStats();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = Main.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
