package data;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class FileCustomDataExporter implements ICustomDataExporter {
    private String filePath;
    private BufferedWriter bufferedWriter;

    public FileCustomDataExporter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void createOutputContainer() throws Exception {
        bufferedWriter = new BufferedWriter(new FileWriter(filePath));
    }

    @Override
    public void finishDataExport() throws Exception {
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    @Override
    public void addDataToOutputContainer(String data) throws Exception {
        bufferedWriter.write(data);
    }
}
