package algorithms;

public class ArraysAlgos {

    /**
     * Method to compare two sorted list of integers and see if they are the same,
     * while ignoring an item from the second list of integer.
     * This methods is used by some Apriori algorithms.
     * @param itemset1 the first itemset
     * @param itemsets2 the second itemset
     * @param posRemoved  the position of an item that should be ignored from "itemset2" to perform the comparison.
     * @return 0 if they are the same, 1 if itemset is larger according to lexical order,
     *         -1 if smaller.
     */
    public static int sameAs(int [] itemset1, int [] itemsets2, int posRemoved) {
        // a variable to know which item from candidate we are currently searching
        int j=0;
        // loop on items from "itemset"
        for (int k : itemset1) {
            // if it is the item that we should ignore, we skip it
            if (j == posRemoved) {
                j++;
            }
            // if we found the item j, we will search the next one
            if (k == itemsets2[j]) {
                j++;
                // if  the current item from i is larger than j,
                // it means that "itemset" is larger according to lexical order
                // so we return 1
            } else if (k > itemsets2[j]) {
                return 1;
            } else {
                // otherwise "itemset" is smaller so we return -1.
                return -1;
            }
        }
        return 0;
    }
}
