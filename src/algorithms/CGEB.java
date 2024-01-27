package algorithms;

import pattern.itemset.ItemU;
import pattern.itemset.ItemsetU;
import pattern.itemset.UTransactionDatabase;

import java.util.*;

public class CGEB<T extends Comparable<T>> {
    private final UTransactionDatabase<T> database;
    private final double minSup;
    private final double minProbThreshold;
    private List<ItemsetU<T>> candidates;
    private Set<ItemsetU<T>> confirmedPMFIs;

    public CGEB(UTransactionDatabase<T> database, double minSup, double minProbThreshold) {
        this.database = database;
        this.minSup = minSup;
        this.minProbThreshold = minProbThreshold;
        this.candidates = new ArrayList<>();
        this.confirmedPMFIs = new HashSet<>();
    }

    public List<ItemsetU<T>> minePMFIs() {
        // Initialize L with all single attributes
        Set<ItemU<T>> L = new HashSet<>(database.getAllItems());

        int i = 1;
        while (!L.isEmpty()) {
            // Initialize variables for each iteration
            List<ItemsetU<T>> Ci = new ArrayList<>();

            // Process each item in L
            for (ItemU<T> X : L) {
                double E_X = 0.0;
                double Var_X = 0.0;
                int count_X = 0;

                // For each transaction in the database
                for (ItemsetU<T> transaction : database.getTransactions()) {
                    if (transaction.contains(X)) {
                        E_X += X.getProbability();
                        Var_X += X.getProbability() * (1 - X.getProbability());
                        count_X++;
                    }
                }

                // Check if X satisfies lower bound and count conditions
                double lbE_X = calculateLbE(0.06, minProbThreshold);
                if (E_X >= lbE_X && count_X >= minSup) {
                    ItemsetU<T> candidate = new ItemsetU<>();
                    candidate.addItem(X);
                    Ci.add(candidate);
                }
            }

            // Update L for the next iteration
            L = updateL(Ci, L);
            candidates.addAll(Ci);
            i++;
        }

        // Confirm PMFIs from candidates
        confirmedPMFIs = confirmPMFIs(candidates);
        return new ArrayList<>(confirmedPMFIs);
    }

    public static double calculateLbE(double T, double minProbThreshold) {
        double lnMinProbThreshold = Math.log(minProbThreshold);

        double firstTerm = 2 * T - lnMinProbThreshold - Math.sqrt(Math.pow(lnMinProbThreshold, 2) - 8 * minProbThreshold * lnMinProbThreshold);

        return firstTerm / 2;
    }


    private Set<ItemU<T>> updateL(List<ItemsetU<T>> candidates, Set<ItemU<T>> L) {
        Set<ItemU<T>> newL = new HashSet<>();
        for (ItemU<T> item : L) {
            boolean foundInCandidate = false;
            for (ItemsetU<T> candidate : candidates) {
                if (candidate.contains(item)) {
                    foundInCandidate = true;
                    break;
                }
            }
            if (!foundInCandidate) {
                newL.add(item);
            }
        }
        return newL;
    }

    private Set<ItemsetU<T>> confirmPMFIs(List<ItemsetU<T>> candidates) {
        Set<ItemsetU<T>> confirmedPMFIs = new HashSet<>();
        for (ItemsetU<T> candidate : candidates) {
            boolean isPMFI = true; // Initialize as potentially PMFI
            for (ItemsetU<T> otherCandidate : candidates) {
                if (otherCandidate.size() == candidate.size() + 1 &&
                        otherCandidate.allTheSameExceptLastItem(candidate) != null) {
                    // Other candidate is a superset with a different last item
                    // Check if P(otherCandidate) <= P(candidate) + minProbThreshold
                    double candidateProb = computeExpectedSupport(candidate);
                    double otherCandidateProb = computeExpectedSupport(otherCandidate);
                    if (otherCandidateProb > candidateProb + minProbThreshold) {
                        isPMFI = false; // Not PMFI if superset probability exceeds the threshold
                        break;
                    }
                }
            }
            if (isPMFI) {
                confirmedPMFIs.add(candidate);
            }
        }
        return confirmedPMFIs;
    }

    private double computeExpectedSupport(ItemsetU<T> itemset) {
        // Calculate and return the expected support based on individual item probabilities
        double support = 0.0;
        for (ItemU<T> item : itemset.getItems()) {
            support += item.getProbability();
        }
        return support;
    }

    /**
     * Print statistics about the latest execution.
     */
    public void printStats() {
        int transactionsCount = database.getTransactions().size();
        int candidatesCount = candidates.size();
//        int databaseScanCount = candidatesCount > 0 ? candidates.get(0).size() - 1 : 0;

        System.out.println("=============  CGEB - STATS =============");
        System.out.println("Transactions count from the database: " + transactionsCount);
        System.out.println("Candidates count: " + candidatesCount);
//        System.out.println("Database scan count: " + databaseScanCount);
//        System.out.println("The algorithm stopped at size " + (databaseScanCount - 1) +
//                ", because there is no candidate");
        System.out.println("Uncertain itemsets count: " + confirmedPMFIs.size());
    }
}
