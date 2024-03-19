package algorithms;

import pattern.itemset.UncertainTransaction;

import java.io.BufferedWriter;
import java.util.*;

public class WCGEB<T> {
	List<UncertainTransaction<T>> uncertainDB;

	double minSupport;
	double minProbability;

	/**  the number of itemsets found */
	private int itemsetCount;

	/** start time of latest execution */
	protected long startTimestamp;

	/** end time of latest execution */
	protected long endTimestamp;

	/** write to file */
	BufferedWriter writer = null;

	public WCGEB(List<UncertainTransaction<T>> uncertainDB, double minSupport, double minProbability) {
		this.uncertainDB = uncertainDB;
		this.minSupport = minSupport;
		this.minProbability = minProbability;
	}

	public int getTransactionCount() {
		return uncertainDB.size();
	}

	public int getItemsetCount() {
		return itemsetCount;
	}

	/**
	 * A method to generate candidates for frequent itemsets.
	 *
	 * @return         	a set of sets representing the generated candidates
	 */
	public Set<Set<T>> generateCandidates() {
		itemsetCount = 0;
		startTimestamp = System.currentTimeMillis();
		Set<Set<T>> candidates = new HashSet<>();

		// Generate frequent 1-itemsets
		Map<T, Double> itemSupports = getItemSupports();
		Set<Set<T>> frequentSingletons = new HashSet<>();
		for (Map.Entry<T, Double> entry : itemSupports.entrySet()) {
			if (entry.getValue() >= minSupport) {
				Set<T> singleton = new HashSet<>();
				singleton.add(entry.getKey());
				frequentSingletons.add(singleton);
			}
		}
		candidates.addAll(frequentSingletons);

		// Generate frequent k-itemsets (k >= 2) using expectation bound
		Set<Set<T>> prevCandidates = frequentSingletons;
		int k = 2;
		while (!prevCandidates.isEmpty()) {
			Set<Set<T>> nextCandidates = new HashSet<>();
			for (Set<T> candidate : prevCandidates) {
				Set<Set<T>> extensions = generateExtensions(candidate, k, itemSupports);
				nextCandidates.addAll(extensions);
				itemsetCount += extensions.size();
			}
			candidates.addAll(nextCandidates);
			prevCandidates = nextCandidates;
			k++;
		}
		endTimestamp = System.currentTimeMillis();
		return candidates;
	}

	/**
	 * Generate a map of items with their corresponding support values.
	 *
	 * @return         the map of items and their support values
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
	 * Generate extensions from a candidate set by adding new elements based on support values.
	 *
	 * @param  candidate     the current candidate set of elements
	 * @param  k            the size of the desired extension sets
	 * @param  itemSupports a map of items to their support values
	 * @return              a set of sets representing the generated extensions
	 */
	private Set<Set<T>> generateExtensions(Set<T> candidate, int k, Map<T, Double> itemSupports) {
		Set<Set<T>> extensions = new HashSet<>();
		List<T> items = new ArrayList<>(itemSupports.keySet());
		for (T item : items) {
			if (!candidate.contains(item)) {
				Set<T> extension = new HashSet<>(candidate);
				extension.add(item);
				if (extension.size() == k && isFrequentByExpectation(extension, itemSupports)) {
					extensions.add(extension);
				}
			}
		}
		return extensions;
	}

	/**
	 * A description of the entire Java function.
	 *
	 * @param  itemset     description of parameter
	 * @param  itemSupports    description of parameter
	 * @return         	description of return value
	 */
	private boolean isFrequentByExpectation(Set<T> itemset, Map<T, Double> itemSupports) {
		double expectation = 0.0;
		for (T item : itemset) {
			expectation += itemSupports.get(item);
		}
		expectation -= (itemset.size() - 1) * getUnionExpectation(itemset, itemSupports);
		double lowerBound = getLowerBoundExpectation(minSupport, minProbability);
		return expectation >= lowerBound;
	}

	/**
	 * Calculate the union expectation of the given itemset based on the provided item supports.
	 *
	 * @param  itemset       the set of items for which to calculate the union expectation
	 * @param  itemSupports  a map containing the support values for each item in the itemset
	 * @return               the calculated union expectation
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

	private double calculateSupport(Set<T> itemset, Map<T, Double> itemSupports) {
		double support = 0.0;
		for (T item : itemset) {
			support += itemSupports.get(item);
		}
		return support;
	}

	/**
	 * Print statistics about the latest execution.
	 */
	public void printStats() {
		System.out
				.println("=============  CGEB - STATS =============");
		long temps = endTimestamp - startTimestamp;
//		System.out.println(" Total time ~ " + temps + " ms");
		System.out.println(" Transactions count from database : " + UncertainTransaction.transaction);
		System.out.println(" Uncertain itemsets count : " + itemsetCount);
		System.out.println(" Total time ~ " + temps + " ms");
		System.out
				.println("===================================================");
	}
}
