package ultil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateProb {

    private String inputFilePath;
    private String outputFilePath;

    public GenerateProb(String inputFilePath, String outputFilePath) {
        this.inputFilePath = inputFilePath;
        this.outputFilePath = outputFilePath;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public String RandomProbProvider(GenerateProb generateProb) {
        try (BufferedReader reader = new BufferedReader(new FileReader(generateProb.getInputFilePath()));
             FileWriter writer = new FileWriter(outputFilePath)) {

            // Read each line from T40I10D100K.dat.txt
            String line;
            Random random = new Random();
            while ((line = reader.readLine()) != null) {
                // Process each number in the line
                String[] numbers = line.split("\\s+");
                StringBuilder output = new StringBuilder();
                for (String num : numbers) {
                    double probability = random.nextDouble();
                    output.append(num).append("(").append(String.format("%.2f", probability)).append(") ");
                }

                // Write the result to the output file
                writer.write(output.toString().trim());
                writer.write(System.lineSeparator()); // Move to the next line in the output file
            }

            System.out.println("Random probabilities generated and written to " + generateProb.getOutputFilePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return generateProb.getOutputFilePath();
    }
}
