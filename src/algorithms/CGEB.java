package algorithms;

import pattern.itemset.UncertainTransaction;

import java.util.*;

public class CGEB<T> {
	List<UncertainTransaction<T>> uncertainDB;
	double minSupport;
	double minProbability;

	public CGEB(List<UncertainTransaction<T>> uncertainDB, double minSupport, double minProbability) {
		this.uncertainDB = uncertainDB;
		this.minSupport = minSupport;
		this.minProbability = minProbability;
	}

	public Set<Set<T>> generateCandidates() {
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
			}
			candidates.addAll(nextCandidates);
			prevCandidates = nextCandidates;
			k++;
		}

		return candidates;
	}

	private Map<T, Double> getItemSupports() {
		Map<T, Double> supports = new HashMap<>();
		for (UncertainTransaction<T> transaction : uncertainDB) {
			for (T item : transaction.items) {
				supports.put(item, supports.getOrDefault(item, 0.0) + transaction.probability);
			}
		}
		return supports;
	}

	private Set<Set<T>> generateExtensions(Set<T> candidate, int k, Map<T, Double> itemSupports) {
		Set<Set<T>> extensions = new HashSet<>();
		List<T> items = new ArrayList<>(itemSupports.keySet());
		for (int i = 0; i < items.size(); i++) {
			T item = items.get(i);
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

	private boolean isFrequentByExpectation(Set<T> itemset, Map<T, Double> itemSupports) {
		double expectation = 0.0;
		for (T item : itemset) {
			expectation += itemSupports.get(item);
		}
		expectation -= (itemset.size() - 1) * getUnionExpectation(itemset, itemSupports);
		double lowerBound = getLowerBoundExpectation(minSupport, minProbability);
		return expectation >= lowerBound;
	}

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
}