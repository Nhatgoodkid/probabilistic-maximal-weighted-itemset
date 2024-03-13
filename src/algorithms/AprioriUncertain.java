package algorithms;

import pattern.itemset.UncertainTransaction;

import java.util.*;


class AprioriUncertain<T> {
	List<UncertainTransaction<T>> uncertainDB;
	double minSupport;
	double minProbability;

	AprioriUncertain(List<UncertainTransaction<T>> uncertainDB, double minSupport, double minProbability) {
		this.uncertainDB = uncertainDB;
		this.minSupport = minSupport;
		this.minProbability = minProbability;
	}

	/**
	 * A method to implement the Apriori algorithm for frequent itemset mining.
	 *
	 * @return         	a set of frequent itemsets
	 */
	Set<Set<T>> apriori() {
		Set<Set<T>> frequentItemsets = new HashSet<>();

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
		frequentItemsets.addAll(frequentSingletons);

		// Generate frequent k-itemsets (k >= 2)
		Set<Set<T>> candidates = frequentSingletons;
		int k = 2;
		while (!candidates.isEmpty()) {
			Set<Set<T>> nextCandidates = generateCandidates(candidates, k);
			Set<Set<T>> frequentKItemsets = new HashSet<>();
			for (Set<T> candidate : nextCandidates) {
				if (isFrequent(candidate, itemSupports)) {
					frequentKItemsets.add(candidate);
				}
			}
			frequentItemsets.addAll(frequentKItemsets);
			candidates = frequentKItemsets;
			k++;
		}

		return frequentItemsets;
	}

	/**
	 * Retrieves the support for each item in the uncertain transaction database.
	 *
	 * @return         	the map of items and their corresponding support values
	 */
	private Map<T, Double> getItemSupports() {
		Map<T, Double> supports = new HashMap<>();
		for (UncertainTransaction<T> transaction : uncertainDB) {
			for (T item : transaction.items) {
				supports.put(item, supports.getOrDefault(item, 0.0) + transaction.probability);
			}
		}
		return supports;
	}

	/**
	 * Generate candidate sets of size k based on the previous candidates.
	 *
	 * @param  prevCandidates  the set of previous candidates
	 * @param  k               the size of the candidate sets to generate
	 * @return                 the set of candidate sets of size k
	 */
	private Set<Set<T>> generateCandidates(Set<Set<T>> prevCandidates, int k) {
		Set<Set<T>> candidates = new HashSet<>();
		List<Set<T>> prevCandidatesList = new ArrayList<>(prevCandidates);
		for (int i = 0; i < prevCandidatesList.size(); i++) {
			for (int j = i + 1; j < prevCandidatesList.size(); j++) {
				Set<T> candidate = union(prevCandidatesList.get(i), prevCandidatesList.get(j));
				if (candidate.size() == k && isSubsetFrequent(candidate, prevCandidates)) {
					candidates.add(candidate);
				}
			}
		}
		return candidates;
	}

	/**
	 * Union of two sets.
	 *
	 * @param  set1   the first set
	 * @param  set2   the second set
	 * @return        the union of set1 and set2
	 */
	private Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> union = new HashSet<>(set1);
		union.addAll(set2);
		return union;
	}

	/**
	 * Check if a candidate set is frequent in the previous candidates.
	 *
	 * @param  candidate       the candidate set
	 * @param  prevCandidates  the set of previous candidates
	 * @return                 true if the candidate is frequent, false otherwise
	 */
	private boolean isSubsetFrequent(Set<T> candidate, Set<Set<T>> prevCandidates) {
		for (int i = 0; i < candidate.size(); i++) {
			Set<T> subset = new HashSet<>(candidate);
			subset.remove(subset.iterator().next());
			if (!prevCandidates.contains(subset)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the given itemset is frequent based on the provided item supports.
	 *
	 * @param  itemset       the set of items to be checked for frequency
	 * @param  itemSupports  a map containing the support values for each item
	 * @return               true if the itemset is frequent, false otherwise
	 */
	private boolean isFrequent(Set<T> itemset, Map<T, Double> itemSupports) {
		double support = 0.0;
		for (T item : itemset) {
			support += itemSupports.get(item);
		}
		support -= (itemset.size() - 1) * getUnionProbability(itemset, itemSupports);
		return support >= minSupport;
	}

	/**
	 * Calculates the union probability of the given itemset based on the item supports.
	 *
	 * @param  itemset       the set of items for which to calculate the union probability
	 * @param  itemSupports  a map containing the support values for each item
	 * @return               the calculated union probability
	 */
	private double getUnionProbability(Set<T> itemset, Map<T, Double> itemSupports) {
		double unionProbability = 1.0;
		for (T item : itemset) {
			unionProbability *= itemSupports.get(item);
		}
		return unionProbability;
	}

//	public static void main(String[] args) {
//		List<pattern.itemset.UncertainTransaction> uncertainDB = new ArrayList<>();
//
//		try {
//			pattern.itemset.UncertainTransaction.loadFile(fileToPath("test.txt"), uncertainDB);
////			pattern.itemset.UncertainTransaction.loadFile(".//T40I10D100K_with_P.dat.txt", uncertainDB);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		double minSupport = 0.4;
//		double minProbability = 0.6;
//
//		algorithms.AprioriUncertain apriori = new algorithms.AprioriUncertain(uncertainDB, minSupport, minProbability);
//		Set<Set<Integer>> frequentItemsets = apriori.apriori();
//
//		System.out.println("Frequent Itemsets:");
//		for (Set<Integer> itemset : frequentItemsets) {
//			System.out.println(itemset);
//		}
//	}

//	public static String fileToPath(String filename) throws UnsupportedEncodingException {
//		URL url = Main.class.getResource(filename);
//		return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}