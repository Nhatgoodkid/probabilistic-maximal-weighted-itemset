import pattern.itemset.UTransactionDatabase;
import util.GenerateProb;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainGenerateProb {

    public static void main(String [] arg) throws IOException{

        String inputPath= "dataset/T40I10D100K/T40I10D100K.dat.txt";
        String inputWithProbPath = ".//T40I10D100K_with_P_W.dat.txt";
        // Loading the binary context
        UTransactionDatabase context = new UTransactionDatabase();

        // Generate probability
        GenerateProb generateProb = new GenerateProb(fileToPath(inputPath), inputWithProbPath);
        String afterGen = generateProb.RandomProbProvider(generateProb);

        try {
            context.loadFile(afterGen);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        context.printDatabase();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = Main.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");    }
}


