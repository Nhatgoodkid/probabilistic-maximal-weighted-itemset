package pattern.itemset;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UTransactionDatabase<T extends Comparable<T>> {


    // this is the set of items in the database
    private final Set<ItemU<T>> allItems = new HashSet<>();
    // this is the list of transactions in the database
    private final List<ItemsetU<T>> transactions = new ArrayList<>();

    /**
     * Load a transaction database from a file.
     *
     * @param path the path of the file
     * @throws IOException exception if error while reading the file.
     */
    public void loadFile(String path) throws IOException {
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
                processTransactions(thisLine.split(" "));
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

    private <T> T parseItemID(String itemString) {
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

    private void processTransactions(String itemsString[]) {
        // We assume that there is no empty line

        // create a new itemset oject representing the transaction
        ItemsetU<T> transaction = new ItemsetU<T>();
        // for each item
        for (String itemString : itemsString) {
            // get the position of left parenthesis and right parenthesis
            int indexOfLeftParanthesis = itemString.indexOf('(');
            int indexOfRightParanthesis = itemString.indexOf(')');
            // get the item ID
            T itemID = parseItemID(itemString.substring(0,
                    indexOfLeftParanthesis));
            // get the existential probability
            double value = Double.parseDouble(itemString.substring(
                    indexOfLeftParanthesis + 1, indexOfRightParanthesis));
            // create an item
            ItemU<T> item = new ItemU<T>(itemID, value);
            // add it to the transaction
            transaction.addItem(item);
            // add it to the set of all items
            allItems.add(item);
        }
        // add the itemset to the transaction to the in-memory database
        transactions.add(transaction);
    }

    /**
     * Print this database to System.out.
     */
    public void printDatabase() {
        System.out
                .println("===================  UNCERTAIN DATABASE ===================");
        int count = 0;
        // for each transaction
        for (ItemsetU<T> itemset : transactions) {
            // print the transaction
            System.out.print("0" + count + ":  ");
            itemset.print();
            System.out.println("");
            count++;
        }
    }

    /**
     * Get the number of transactions.
     *
     * @return a int
     */
    public int size() {
        return transactions.size();
    }

    /**
     * Get the list of transactions.
     *
     * @return the list of Transactions.
     */
    public List<ItemsetU<T>> getTransactions() {
        return transactions;
    }

    /**
     * Get the set of items in this database.
     *
     * @return a Set of Integers
     */
    public Set<ItemU<T>> getAllItems() {
        return allItems;
    }

}