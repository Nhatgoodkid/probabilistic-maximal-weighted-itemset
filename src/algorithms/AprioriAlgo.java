package algorithms;


import pattern.itemset.ItemU;
import pattern.itemset.ItemsetU;
import pattern.itemset.UTransactionDatabase;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class AprioriAlgo<T extends Comparable<T>> {

    /** this is the database */
    protected UTransactionDatabase<T> database;

    /** variable indicating the current level for the Apriori generation
     // (itemsets of size k) */
    protected int k;

    /** number of candidates generated */
    protected int totalCandidateCount = 0;

    /**  number of database scan */
    protected int databaseScanCount = 0;

    /** start time of latest execution */
    protected long startTimestamp;

    /** end time of latest execution */
    protected long endTimestamp;

    /**  the number of itemsets found */
    private int itemsetCount;

    /** write to file */
    BufferedWriter writer = null;

    /** Special parameter to set the maximum size of itemsets to be discovered */
    int maxItemsetSize = Integer.MAX_VALUE;

    /**
     * Constructor
     * @param database the database for applying this algorithm
     */
    public AprioriAlgo(UTransactionDatabase<T> database) {
        this.database = database;
    }

    /**
     * Run this algorithm
     * @param minsupp  a minimum support threshold
     * @param output  the output file path for writing the result
     * @throws IOException exception if error reading/writing files
     */
    public void runAlgorithm(double minsupp, String output) throws IOException {
        // record start time
        startTimestamp = System.currentTimeMillis();
        // reset variables for statistics
        totalCandidateCount = 0;
        databaseScanCount = 0;
        itemsetCount=0;

        // prepare the output file
        writer = new BufferedWriter(new FileWriter(output));

        // Generate candidates with size k = 1 (all itemsets of size 1)
        k = 1;
        Set<ItemsetU<T>> candidatesSize1 = generateCandidateSize1();

        // increase the number of candidates generated
        totalCandidateCount+=candidatesSize1.size();

        // calculate the support of each candidate of size 1
        // by scanning the database
        calculateSupportForEachCandidate(candidatesSize1);

        // To build level 1, we keep only the frequent candidates.
        // We scan the database one time to calculate the support of each candidate.
        Set<ItemsetU<T>> level = createLevelWithFrequentCandidates(minsupp,
                candidatesSize1);

        // Now this is the recursive step
        // itemsets of size k will be generated recursively starting from k=2
        //  by using itemsets of size k-1 until no candidates
        // can be generated
        k = 2;
        Set<ItemsetU<T>> previousLevel = new HashSet<>();

        // While the level is not empty
        while (!level.isEmpty()  && k <= maxItemsetSize) {
            // Generate candidates of size K
            Set<ItemsetU<T>> candidatesK = generateCandidateSizeK(level);
            // increase the candidate count

            totalCandidateCount+=candidatesK.size();

            // We scan the database one time to calculate the support
            // of each candidates.
            calculateSupportForEachCandidate(candidatesK);

            // We build the level k+1 with all the candidates that have
            // a support higher than the minsup threshold.
            Set<ItemsetU<T>> levelK = createLevelWithFrequentCandidates(
                    minsupp, candidatesK);

            level = levelK; // We keep only the last level...'
            k++;
        }

        // close the output file
        writer.close();
        // record end time
        endTimestamp = System.currentTimeMillis();
    }


    /**
     * Save an itemset to the output file.
     * @param itemset  the itemset
     * @throws IOException exception if error writing the itemset to the file
     */
    private void saveItemsetToFile(ItemsetU<T> itemset) throws IOException{
//        writer.write(itemset.toString() + " #SUP: " + itemset.getExpectedSupport());
        writer.write(itemset.toString());
        writer.newLine();
        itemsetCount++;
    }

    /**
     * Take a set of candidates and compare them with the min expected support to keep
     * only the itemset meeting that  minimum threshold.
     * @param minsupp  the minimum expected threshold
     * @param candidatesK  a set of itemsets of size k
     * @return  the set of frequent itemsets of size k
     * @throws IOException exception if error writing output file
     */
    protected Set<ItemsetU<T>> createLevelWithFrequentCandidates(double minsupp,Set<ItemsetU<T>> candidatesK) throws IOException {
        Set<ItemsetU<T>> levelK = new HashSet<ItemsetU<T>>();
        // for each itemset
        for (ItemsetU<T> candidate : candidatesK) {
            // check if it has enough support
            if (candidate.getExpectedSupport() >= minsupp && maxItemsetSize >=1) {
                // if yes add it to the set of frequent itemset of size k

                levelK.add(candidate);

                // save the itemset to the output file
                saveItemsetToFile(candidate);

            }
        }
        // return frequent k-itemsets
        return levelK;
    }

    /**
     * Calculate the support of a set of candidates by scanning the database.
     * @param candidatesK  a set of candidates of size k
     */
    protected void calculateSupportForEachCandidate(Set<ItemsetU<T>> candidatesK) {
        // increase database scan count
        databaseScanCount++;

        for (ItemsetU<T> transaction : database.getTransactions()) {
            for (ItemsetU<T> candidate : candidatesK) {
                double expectedSupport = calculateExpectedSupport(candidate, transaction);
                candidate.increaseSupportBy(expectedSupport);
            }
        }
    }

    private double calculateExpectedSupport(ItemsetU<T> candidate, ItemsetU<T> transaction) {
        double expectedSupport = 0;

        for (ItemU<T> item : candidate.getItems()) {
            boolean found = false;

            for (ItemU<T> itemT : transaction.getItems()) {
                if (item.getId() == itemT.getId()) {
                    found = true;
                    expectedSupport = (expectedSupport == 0) ? itemT.getProbability() : expectedSupport * itemT.getProbability();
                    break;
                } else if (item.getId().compareTo(itemT.getId()) < 0) {
                    break;
                }
            }

            if (!found) {
                return 0; // If any item is not found, expected support is 0
            }
        }

        return expectedSupport;
    }


    /**
     * Generate candidate itemsets containing a single item.
     * @return a set of candidate itemsets
     */
    protected Set<ItemsetU<T>> generateCandidateSize1() {
        // create the set of candidates as empty
        Set<ItemsetU<T>> candidates = new HashSet<>();
        // for each item
        for (ItemU<T> item : database.getAllItems()) {
            // simply add it to the set of candidates
            ItemsetU<T> itemset = new ItemsetU<>();
            itemset.addItem(item);
            candidates.add(itemset);
        }
        return candidates;
    }

    /**
     * Generate candidate itemsets of size K by using HWTUIs of size k-1
     * @param levelK_1   itemsets of size k-1
     * @return  candidates of size K
     */
    protected Set<ItemsetU<T>> generateCandidateSizeK(Set<ItemsetU<T>> levelK_1) {
        // a set to store candidates
        Set<ItemsetU<T>> candidates = new HashSet<ItemsetU<T>>();
        // For each itemset I1 and I2 of level k-1
        Object[] itemsets = levelK_1.toArray();

        for(int i=0; i< levelK_1.size(); i++){
            ItemsetU<T> itemset1 = (ItemsetU<T>)itemsets[i];
            System.out.println(itemset1);
            for(int j=0; j< levelK_1.size(); j++){
                ItemsetU<T> itemset2 = (ItemsetU<T>)itemsets[j];
                // If I1 is smaller than I2 according to lexical order and
                // they share all the same items except the last one.
                ItemU<T> missing = itemset1.allTheSameExceptLastItem(itemset2);
                if(missing != null){
                    // Then, create a new candidate by combining itemset1 and itemset2
                    ItemsetU<T> candidate = new ItemsetU<T>();
                    for(ItemU<T> item : itemset1.getItems()){
                        candidate.addItem(item);
                    }
                    candidate.addItem(missing);

                    // The candidate is tested to see if its subsets of size k-1 are included in
                    // level k-1 (they are frequent).
                    if(allSubsetsOfSizeK_1AreFrequent(candidate,levelK_1)){
                        // if it pass the test, add it to the set of candidates
                        candidates.add(candidate);
                    }
                }
            }
        }
        // return the set of candidates
        return candidates;
    }

    /**
     * Check if all subsets of size k-1 of a candidate itemset of size k are frequent.
     * @param candidate  the candidate itemset
     * @param levelK_1  frequent itemsets of size k-1
     * @return true if all subsets are frequent, otherwise false
     */
    protected boolean allSubsetsOfSizeK_1AreFrequent(ItemsetU<T> candidate, Set<ItemsetU<T>> levelK_1) {
        // To generate all the set of size K-1, we will proceed
        // by removing each item, one by one.

        //if only one item, return true because the empty set is always frequent
        if(candidate.size() == 1){
            return true;
        }
        // for each item
        for(ItemU<T> item : candidate.getItems()){
            // copy the itemset without this item to get a suset
            ItemsetU<T> subset = candidate.cloneItemSetMinusOneItem(item);
            boolean found = false;
            // we scan itemsets of size k-1
            for(ItemsetU<T> itemset : levelK_1){
                // if we found the subset, then set found to true 
                // and stop this loop
                if(itemset.isEqualTo(subset)){
                    found = true;
                    break;
                }
            }
            // if the subset was not found, then we return false
            if(!found){
                return false;
            }
        }
        // all the subsets were found, so we return true
        return true;
    }

    /**
     * Print statistics about the latest execution.
     */
    public void printStats() {
        System.out
                .println("=============  U-APRIORI - STATS =============");
        long temps = endTimestamp - startTimestamp;
//		System.out.println(" Total time ~ " + temps + " ms");
        System.out.println(" Transactions count from database : "
                + database.size());
        System.out.println(" Candidates count : " + totalCandidateCount);
        System.out.println(" Database scan count : " + databaseScanCount);
        System.out.println(" The algorithm stopped at size " + (k - 1)
                + ", because there is no candidate");
        System.out.println(" Uncertain itemsets count : " + itemsetCount);

        System.out.println(" Total time ~ " + temps + " ms");
        System.out
                .println("===================================================");
    }

    /**
     * Set the maximum pattern length
     * @param length the maximum length
     */
    public void setMaximumPatternLength(int length) {
        this.maxItemsetSize = length;
    }
}
