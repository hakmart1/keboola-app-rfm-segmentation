package data;

import segmentation.RFMResult;
import segmentation.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CsvRFMDataExporter implements IRFMDataExporter {
    private String csvFilePath, separator;
    private BufferedWriter bufferedWriter;
    private boolean considerClusters;
    private DateFormat dateFormarter;

    public CsvRFMDataExporter(String csvFilePath, String separator, boolean considerClusters, String dateFormat) {
        this.csvFilePath = csvFilePath;
        this.separator = separator;
        this.considerClusters = considerClusters;
        this.dateFormarter = new SimpleDateFormat(dateFormat);
    }

    @Override
    public void createOutputContainer() throws IOException {
        bufferedWriter = new BufferedWriter(new FileWriter(csvFilePath));
        if (considerClusters){
            bufferedWriter.write(String.format("cluster%s", separator));
        }
        bufferedWriter.write(String.format("entityId%srecency%srecencySegment%sfrequency%sfrequencySegment%smonetary%smonetarySegment", separator, separator, separator, separator, separator, separator));
        bufferedWriter.newLine();
    }

    @Override
    public void addDataToOutputContainer(User user, RFMResult rfmResult) throws IOException {
        bufferedWriter.write(String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s",
                user.getUserId(), separator,
                dateFormarter.format(user.getRecency()), separator, rfmResult.getRecency(), separator,
                user.getFrequency(), separator, rfmResult.getFrequency(), separator,
                user.getMonetary(), separator, rfmResult.getMonetary()));
        bufferedWriter.newLine();
    }

    @Override
    public void addDataToOutputContainer(String cluster, User user, RFMResult rfmResult) throws IOException {
        bufferedWriter.write(cluster+separator);
        this.addDataToOutputContainer(user, rfmResult);
    }

    @Override
    public void finishDataExport() throws IOException {
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
