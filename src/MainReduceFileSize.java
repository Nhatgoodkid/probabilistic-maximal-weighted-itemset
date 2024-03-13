import pattern.itemset.UTransactionDatabase;
import util.ReduceFileSize;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainReduceFileSize {

    public static void main(String [] arg) throws IOException{

        UTransactionDatabase context = new UTransactionDatabase();

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        context.printDatabase();

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}


