import algorithms.APFI_MAX;
import pattern.itemset.UncertainTransaction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainAPFI_MAX {

	public static void main(String[] args) throws IOException {
		// Load the uncertain database
		List<UncertainTransaction<Integer>> uncertainDB = new ArrayList<>();

//		String inputPath = "dataset/test.txt";
		String inputPath = "dataset/T40I10D100K_with_P_W_1%.dat.txt";
		try {
			UncertainTransaction.loadFile(fileToPath(inputPath), uncertainDB);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the minimum support and minimum probability
		double minSupport = 0.06;
		double minProbability = 0.6;

		// Create an instance of algorithms.APFI_MAX
		APFI_MAX<Integer> apfiMax = new APFI_MAX<>(uncertainDB, minSupport, minProbability);

		// Run the algorithms.APFI_MAX algorithm
		apfiMax.runAPFI_MAX();
		// Print the PMFIs
		savePMFIsToFile("pmfi_output_1%.txt", apfiMax.PMFIs);

		apfiMax.printStats();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = Main.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}

	public static void savePMFIsToFile(String filename, Set<Set<Integer>> PMFIs) {
		try (FileWriter writer = new FileWriter(filename)) {
			writer.write("PMFIs:\n");
			for (Set<Integer> pmfi : PMFIs) {
				writer.write(pmfi.toString() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
