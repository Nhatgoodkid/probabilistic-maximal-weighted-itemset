import algorithms.AprioriAlgo;
import algorithms.CGEB;
import pattern.itemset.UTransactionDatabase;
import util.GenerateProb;
import util.ReduceFileSize;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class Main {

    public static void main(String [] arg) throws IOException{

        String inputPath= "T40I10D100K.dat.txt";
        String inputWithProbPath = ".//T40I10D100K_with_P.dat.txt";
        // Loading the binary context
        UTransactionDatabase context = new UTransactionDatabase();

        // Generate probability
        GenerateProb generateProb = new GenerateProb(fileToPath(inputPath), inputWithProbPath);
        String afterGen = generateProb.RandomProbProvider(generateProb);

        // File Path dataset you need to reduce
        String originalFilePath = ".//T40I10D100K_with_P.dat.txt";
        // Output you want to save after success reduced size
        String reducedFilePath = "reducedFile.txt";
        // Change percentage depends on requirement
        double percentage = 0.2;

        ReduceFileSize reduceFileSize = new ReduceFileSize(originalFilePath, reducedFilePath, percentage);
        // Reduce large dataset with a certain percentage
        reducedFilePath = reduceFileSize.reduceFileSize(reduceFileSize);

        try {
            context.loadFile(reducedFilePath);
//            context.loadFile(fileToPath("test.txt"));
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


