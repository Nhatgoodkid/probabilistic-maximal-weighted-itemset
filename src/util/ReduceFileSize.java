package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ReduceFileSize {
    private String originFilePath;
    private String reducedFilePath;
    private double percentage;

    public ReduceFileSize(String originFilePath, String reducedFilePath, double percentage) {
        this.originFilePath = originFilePath;
        this.reducedFilePath = reducedFilePath;
        this.percentage = percentage;
    }

    public String getOriginFilePath() {
        return originFilePath;
    }

    public void setOriginFilePath(String originFilePath) {
        this.originFilePath = originFilePath;
    }

    public String getReducedFilePath() {
        return reducedFilePath;
    }

    public void setReducedFilePath(String reducedFilePath) {
        this.reducedFilePath = reducedFilePath;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }


    /**
     * Resize a database to X % of its size
     * @param reduceFileSize contains the input file path
     * the output file path
     * the percentage of the size that the original database that the output database should have
     * @throws IOException if an error while reading/writing files.
     * @throws NumberFormatException if an error while reading the file
     */
    public String reduceFileSize(ReduceFileSize reduceFileSize)  throws IOException, NumberFormatException{
        // Read all lines from the original file
        List<String> allLines = Files.readAllLines(Path.of(reduceFileSize.getOriginFilePath()));

        // Calculate the number of lines to keep
        int numLinesToKeep = (int) (allLines.size() * reduceFileSize.getPercentage());

        // Shuffle the lines randomly
        Collections.shuffle(allLines);

        // Create a sublist containing the selected lines
        List<String> selectedLines = allLines.subList(0, numLinesToKeep);

        // Write the selected lines to the reduced file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reduceFileSize.getReducedFilePath()))) {
            for (String line : selectedLines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return reduceFileSize.getReducedFilePath();
    }
}
