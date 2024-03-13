package algorithms;

import pattern.itemset.UncertainTransaction;

import java.util.*;

public class APFI_MAX<T> {

	List<UncertainTransaction<T>> uncertainDB;
	double minSupport;
	double minProbability;
	public Set<Set<T>> PMFIs;

	public APFI_MAX(List<UncertainTransaction<T>> uncertainDB, double minSupport, double minProbability) {
		this.uncertainDB = uncertainDB;
		this.minSupport = minSupport;
		this.minProbability = minProbability;
		this.PMFIs = new HashSet<>();
	}

	public void runAPFI_MAX() {
		CGEB<T> cgeb = new CGEB<>(uncertainDB, minSupport, minProbability);
		Set<Set<T>> candidates = cgeb.generateCandidates();

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
	}

	boolean isFrequentByEstimation(Set<T> itemset) {
		Map<T, Double> itemSupports = getItemSupports();
		double expectation = getExpectation(itemset, itemSupports);
		double variance = getVariance(itemset, itemSupports);
		double lowerBound = getLowerBoundExpectation(minSupport, minProbability);

		return expectation >= lowerBound && FM(itemset, minSupport, minProbability, expectation, variance);
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

	private double getExpectation(Set<T> itemset, Map<T, Double> itemSupports) {
		double expectation = 0.0;
		for (T item : itemset) {
			expectation += itemSupports.get(item);
		}
		expectation -= (itemset.size() - 1) * getUnionExpectation(itemset, itemSupports);
		return expectation;
	}

	private double getVariance(Set<T> itemset, Map<T, Double> itemSupports) {
		double variance = 0.0;
		for (T item : itemset) {
			variance += itemSupports.get(item) * (1 - itemSupports.get(item));
		}
		return variance;
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
