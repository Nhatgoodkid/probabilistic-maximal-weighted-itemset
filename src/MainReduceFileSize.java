import pattern.itemset.UTransactionDatabase;
import util.ReduceFileSize;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class MainReduceFileSize {

    public static void main(String [] arg) throws IOException{

        UTransactionDatabase context = new UTransactionDatabase();

        // File Path dataset you need to reduce
        String originalFilePath = "dataset/T10I4D100K/T10I4D100K_with_P_W.dat.txt";
        // Output you want to save after success reduced size
        String reducedFilePath = ".//src/dataset/T10I4D100K/T10I4D100K_with_P_W_40%.dat.txt";
        // Change percentage depends on requirement
        double percentage = 0.4;

        ReduceFileSize reduceFileSize = new ReduceFileSize(fileToPath(originalFilePath), reducedFilePath, percentage);
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
        String decodedPath = URLDecoder.decode(MainReduceFileSize.class.getResource(filename).getFile(), "UTF-8");
        return new File(decodedPath).getPath();
    }

}


