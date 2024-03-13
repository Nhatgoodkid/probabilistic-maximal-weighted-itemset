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
				// if the line is a comment, is empty, or is a kind of metadata
				if (thisLine.isEmpty() || thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%' || thisLine.charAt(0) == '@') {
					continue;
				}

				// process the transaction
				List<UncertainTransaction<T>> transactions = parseTransaction(thisLine);

				// Add transactions to uncertainDB
				uncertainDB.addAll(transactions);
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

	private static <T> List<UncertainTransaction<T>> parseTransaction(String transactionLine) {
		List<UncertainTransaction<T>> transactions = new ArrayList<>();

		// Use a regular expression to separate items and probability
		Pattern pattern = Pattern.compile("([\\w\\d]+)\\((\\d*\\.?\\d+)\\)");
		Matcher matcher = pattern.matcher(transactionLine);

		while (matcher.find()) {
			T item = parseItemID(matcher.group(1));
			double probability = Double.parseDouble(matcher.group(2));
			List<T> items = new ArrayList<>();
			items.add(item);
			transactions.add(new UncertainTransaction<>(items, probability));
		}

		return transactions;
	}

	private static <T> T parseItemID(String itemString) {
		// Check if data Type of ID
		// if ID is Integer
		try {
			return (T) Integer.valueOf(itemString);
		} catch (NumberFormatException e) {
			// if not Integer, return as String
			return (T) itemString;
		}
	}
}
