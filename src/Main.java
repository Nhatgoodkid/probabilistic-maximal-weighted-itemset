import algorithms.AprioriAlgo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class Main {

    public static void main(String [] arg) throws IOException {

        String input = fileToPath("test.txt");
        String output = ".//output.txt";  // the path for saving the frequent itemsets found

        double minsup = 0.4; // means a minsup of 2 transaction (we used a relative support)

        // Applying the Apriori algorithm
        AprioriAlgo algo = new AprioriAlgo();

        algo.runAlgorithm(minsup, input, output);
        algo.printStats();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}


