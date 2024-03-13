package pattern.itemset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UncertainTransaction<T> {
	public List<T> items;
	public double probability;

	UncertainTransaction(List<T> items, double probability) {
		this.items = items;
		this.probability = probability;
	}

	public static <T> void loadFile(String path, List<UncertainTransaction<T>> uncertainDB) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			// for each transaction (line) in the input file
			while ((thisLine = myInput.readLine()) != null) {
				// if the line is  a comment, is  empty or is a
				// kind of metadata
				if (thisLine.isEmpty() == true ||
						thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
						|| thisLine.charAt(0) == '@') {
					continue;
				}

				// process the transaction
				List<Object> items = new ArrayList<>();
				double probability = 0.0;

				// Use a regular expression to separate items and probability
				Pattern pattern = Pattern.compile("^(.*) \\(([^)]*)\\)$");
				Matcher matcher = pattern.matcher(thisLine);
				if (matcher.matches()) {
					String itemsString = matcher.group(1).trim();
					String probString = matcher.group(2);

					// Parse items
					for (String itemString : itemsString.split("\\s+")) {
						items.add(parseItemID(itemString));
					}

					// Parse probability
					probability = Double.parseDouble(probString);
				}

				// Create pattern.itemset.UncertainTransaction object and add it to the list
				uncertainDB.add(new UncertainTransaction(items, probability));

			}
		} catch (Exception e) {
			// catch exceptions
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				// close the file
				myInput.close();
			}
		}
	}


	private static <T> T parseItemID(String itemString) {
		// Check if data Type of ID
		// if ID is Integer
		if (itemString.matches("\\d+")) {
			return (T) Integer.valueOf(itemString);
		}
		// if ID is String
		else {
			return (T) itemString;
		}
	}


}