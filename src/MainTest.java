import algorithms.AprioriAlgo;
import pattern.itemset.UTransactionDatabase;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTest {

    public static void main(String [] arg) throws IOException{

        String inputPath= "test.txt";

        // Loading the binary context
        UTransactionDatabase context = new UTransactionDatabase();

        try {
            context.loadFile(fileToPath(inputPath));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        context.printDatabase();

        String output = ".//output2.txt";

        // Applying the UApriori algorithm
        AprioriAlgo algo = new AprioriAlgo(context);

        // Uncomment the following line to set the maximum pattern length (number of items per itemset)
//		algo.setMaximumPatternLength(2);
        double minSup = 0.1; //

        algo.runAlgorithm(minSup, output);
        algo.printStats();

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}


