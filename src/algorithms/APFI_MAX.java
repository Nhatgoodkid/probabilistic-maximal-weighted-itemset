package algorithms;

import pattern.itemset.UncertainTransaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import util.MemoryLogger;

public class APFI_MAX<T> {

	List<UncertainTransaction<T>> uncertainDB;
	double minSupport;
	double minProbability;
	public Set<Set<T>> PMFIs;


	/** start time of latest execution */
	protected long startTimestamp;

	/** end time of latest execution */
	protected long endTimestamp;

	/**  the number of itemsets found */
	private int itemsetCount;

	public APFI_MAX(List<UncertainTransaction<T>> uncertainDB, double minSupport, double minProbability) {
		this.uncertainDB = uncertainDB;
		this.minSupport = minSupport;
		this.minProbability = minProbability;
		this.PMFIs = new HashSet<>();
	}

	/**
	 * Runs the APFI-MAX algorithm to mine frequent multiple-itemsets from the provided uncertain database.
	 * This method performs candidate generation, estimation, and confirmation steps to identify frequent itemsets
	 * satisfying the minimum support and minimum probability thresholds.
	 */
	public void runAPFI_MAX(String output) throws IOException {
		CGEB<T> cgeb = new CGEB<>(uncertainDB, minSupport, minProbability);
		Set<Set<T>> candidates = cgeb.generateCandidates();
		int itemsetCount = cgeb.getItemsetCount(); // Lấy số lượng itemset từ CGEB
		int transactionCount = cgeb.getTransactionCount(); // Lấy số lượng giao dịch từ CGEB
		cgeb.printStats();
		MemoryLogger.getInstance().reset();
		startTimestamp = System.currentTimeMillis();

		List<Set<T>> frequentItemsets = new ArrayList<>();
		List<Set<T>> candidatesOfLength = new ArrayList<>();

		// Top-down confirmation
		for (int length = candidates.size(); length > 0; length--) {
			for (Set<T> candidate : candidates) {
				if (candidate.size() == length) {
					candidatesOfLength.add(candidate);
				}
			}

			for (Set<T> candidate : candidatesOfLength) {
				if (isFrequentByEstimation(candidate)) {
					frequentItemsets.add(candidate);
					PMFIs.add(candidate);
					for (Set<T> subset : frequentItemsets) {
						if (subset.size() == length - 1 && candidate.containsAll(subset)) {
							frequentItemsets.remove(subset);
						}
					}
				}
			}

			candidatesOfLength.clear();
		}
		endTimestamp = System.currentTimeMillis();
		MemoryLogger.getInstance().checkMemory();
		writePMFIsToFile(output, itemsetCount, transactionCount);
	}

	/**
	 * Write PMFIs and timestamp to a file.
	 *
	 * @param outputFilename The name of the file to write to.
	 * @throws IOException If an I/O error occurs while writing to the file.
	 */
	private void writePMFIsToFile(String outputFilename, int itemsetCount, int transactionCount) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
		writer.write("Running time: " + (endTimestamp - startTimestamp)  + " ms\n");
		writer.write("Maximum memory usage : " + MemoryLogger.getInstance().getMaxMemory() + " mb\n");
		writer.write("Itemset count: " + itemsetCount + "\n");
		writer.write("Transaction count: " + transactionCount + "\n");
		writer.write("minPro: " + this.minProbability + "\n");
		writer.write("minSup: " + this.minSupport + "\n");
		writer.write("PMFIs:\n");
		for (Set<T> itemset : PMFIs) {
			writer.write(itemset.toString() + "\n");
		}
		writer.close();
	}

	/**
	 * Checks if a given itemset is frequent based on estimation using its expected support and variance.
	 * This method calculates the expectation and variance of the itemset's support and applies the FM test
	 * to compare them against the minimum support and minimum probability thresholds.
	 *
	 * @param itemset the itemset to check for frequency
	 * @return true if the itemset is estimated to be frequent, false otherwise
	 */
	boolean isFrequentByEstimation(Set<T> itemset) {
		Map<T, Double> itemSupports = getItemSupports();
		double expectation = getExpectation(itemset, itemSupports);
		double variance = getVariance(itemset, itemSupports);
		double lowerBound = getLowerBoundExpectation(minSupport, minProbability);

		return expectation >= lowerBound && FM(itemset, minSupport, minProbability, expectation, variance);
	}

	/**
	 * Calculates the support for each item in the uncertain database.
	 * This method iterates through all transactions and sums the probabilities of each item across transactions.
	 *
	 * @return a map where keys are items and values are their corresponding total support (sum of probabilities)
	 */
	private Map<T, Double> getItemSupports() {
		Map<T, Double> supports = new HashMap<>();
		for (UncertainTransaction<T> transaction : uncertainDB) {
			for (T item : transaction.items) {
				supports.put(item, supports.getOrDefault(item, 0.0) + transaction.probability * transaction.weight);
			}
		}
		return supports;
	}

	/**
	 * Calculates the expected support of a given itemset based on the item supports in the database.
	 * This method sums the individual item supports in the itemset, then subtracts the expected support
	 * of the union of all items (adjusted for itemset size).
	 *
	 * @param itemset the itemset for which to calculate expectation
	 * @param itemSupports a map containing the support for each item
	 * @return the expected support of the itemset
	 */
	private double getExpectation(Set<T> itemset, Map<T, Double> itemSupports) {
		double expectation = 0.0;
		for (T item : itemset) {
			expectation += itemSupports.get(item);
		}
		expectation -= (itemset.size() - 1) * getUnionExpectation(itemset, itemSupports);
		return expectation;
	}

	/**
	 * Calculates the variance of the support for a given itemset based on the item supports in the database.
	 * This method iterates through each item in the itemset, calculates the product of its support and
	 * (1 - support), and sums these values to represent the variance.
	 *
	 * @param itemset the itemset for which to calculate variance
	 * @param itemSupports a map containing the support for each item
	 * @return the variance of the itemset's support
	 */
	private double getVariance(Set<T> itemset, Map<T, Double> itemSupports) {
		double variance = 0.0;
		for (T item : itemset) {
			variance += itemSupports.get(item) * (1 - itemSupports.get(item));
		}
		return variance;
	}

	/**
	 * Calculate the union expectation for a given itemset based on the item supports.
	 *
	 * @param  itemset      the set of items to calculate the union expectation for
	 * @param  itemSupports a map containing items as keys and their supports as values
	 * @return the calculated union expectation value
	 */
	private double getUnionExpectation(Set<T> itemset, Map<T, Double> itemSupports) {
		double unionExpectation = 1.0;
		for (T item : itemset) {
			unionExpectation *= itemSupports.get(item);
		}
		return unionExpectation;
	}

	private double getLowerBoundExpectation(double minSupport, double minProbability) {
		return 2 * minSupport - Math.log(minProbability) - Math.sqrt(Math.log(1 / minProbability) * (Math.log(1 / minProbability) - 8 * minSupport * Math.log(minProbability))) / 2;
	}

	/**
	 * Applies the FM test to determine if a given itemset is likely frequent based on its estimated support and variance.
	 *
	 * This method calculates the upper bound for expected support and compares it to the actual expectation.
	 * If the expectation is greater than or equal to the upper bound, the itemset is considered frequent without further evaluation.
	 * Otherwise, the FM test is performed using the standard normal distribution. It calculates the standard normal value
	 * based on the difference between the minimum support and the itemset's expectation, then uses the cumulative distribution function (CDF)
	 * of the standard normal distribution to get the frequency. The itemset is considered frequent if this frequency is greater than or equal to the minimum probability threshold.
	 *
	 * @param itemset the itemset to evaluate
	 * @param minSupport the minimum support threshold
	 * @param minProbability the minimum probability threshold
	 * @param expectation the expected support of the itemset
	 * @param variance the variance of the itemset's support
	 * @return true if the itemset is estimated to be frequent based on the FM test, false otherwise
	 */
	private boolean FM(Set<T> itemset, double minSupport, double minProbability, double expectation, double variance) {
		double upperBound = getUpperBoundExpectation(minSupport, minProbability);
		if (expectation >= upperBound) {
			return true;
		}

		double standardNormalValue = (minSupport - expectation) / Math.sqrt(variance);
		double frequency = 1 - NormalDistribution.cdf(standardNormalValue);
		return frequency >= minProbability;
	}

	private double getUpperBoundExpectation(double minSupport, double minProbability) {
		return minSupport - Math.log(1 - minProbability) + Math.sqrt(Math.log(1 - minProbability) * (Math.log(1 - minProbability) - 2 * minSupport * Math.log(1 - minProbability)));
	}


	/**
	 * Print statistics about the latest execution.
	 */
	public void printStats() {
		System.out
				.println("=============  APFI-MAX - STATS =============");
		long temps = endTimestamp - startTimestamp;
//		System.out.println(" Total time ~ " + temps + " ms");

		System.out.println(" Total time ~ " + temps + " ms");
		System.out
				.println("===================================================");
	}

	/**
	 * This class provides utility methods for calculations involving the standard normal distribution.
	 * It is used internally by the APFI-MAX algorithm.
	 */
	private static class NormalDistribution {
		private static final double SQRT_2PI = Math.sqrt(2 * Math.PI);

		public static double cdf(double x) {
			return 0.5 * (1 + erf(x / SQRT_2PI));
		}

		private static double erf(double x) {
			double sum = x;
			double term = x;
			int n = 1;

			while (Math.abs(term) > 1e-15) {
				term *= -x * x / (n + 0.5);
				sum += term;
				n++;
			}

			return 2 * sum / Math.sqrt(Math.PI);
		}
	}
}
