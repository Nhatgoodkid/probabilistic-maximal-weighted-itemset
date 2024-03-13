import algorithms.AprioriAlgo;
import pattern.itemset.UTransactionDatabase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class Main {

    public static void main(String [] arg) throws IOException{

        String inputWithProbPath = ".//T40I10D100K_with_P.dat.txt";
        UTransactionDatabase context = new UTransactionDatabase();

        try {
            context.loadFile(inputWithProbPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        context.printDatabase();

        String output = ".//output2.txt";
        // Applying the UApriori algorithm
        AprioriAlgo algo = new AprioriAlgo(context);

//		algo.setMaximumPatternLength(2);
        double minSup = 0.1; //

        algo.runAlgorithm(minSup, output);
        algo.printStats();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}


