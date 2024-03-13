import algorithms.APFI_MAX;
import pattern.itemset.UncertainTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainAPFI_MAX {

	public static void main(String[] args) {
		// Load the uncertain database
		List<UncertainTransaction<String>> uncertainDB = new ArrayList<>();
		try {
			UncertainTransaction.loadFile(fileToPath("T40I10D100K_with_P.dat.txt"), uncertainDB);
//			UncertainTransaction.loadFile(fileToPath("test.txt"), uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the minimum support and minimum probability
		double minSupport = 0.001;
		double minProbability = 0.6;

		// Create an instance of algorithms.APFI_MAX
		APFI_MAX<String> apfiMax = new APFI_MAX<>(uncertainDB, minSupport, minProbability);

		// Run the algorithms.APFI_MAX algorithm
		apfiMax.runAPFI_MAX();
		// Print the PMFIs
		System.out.println("PMFIs:");
		for (Set<String> pmfi : apfiMax.PMFIs) {
			System.out.println(pmfi);
		}
		apfiMax.printStats();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = Main.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
}
