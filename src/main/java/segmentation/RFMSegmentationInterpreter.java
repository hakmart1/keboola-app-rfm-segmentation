package segmentation;

import data.ICustomDataExporter;

import java.util.Date;
import java.util.Map;

public abstract class RFMSegmentationInterpreter {

    protected ICustomDataExporter dataExporter;

    public RFMSegmentationInterpreter(ICustomDataExporter dataExporter){
        this.dataExporter = dataExporter;
    }

    public void prepareInterpretation(boolean isClustered) throws Exception {
        dataExporter.createOutputContainer();
        dataExporter.addDataToOutputContainer((isClustered?"cluster,":"") + "pessimistic,realistic,optimistic"+System.lineSeparator());
    }

    public void processSegmentation(Map<User, RFMResult> rfmResults, Date[] recencyThresholds, Integer[] frequencyThresholds, Double[] monetaryThresholds) throws Exception {};
    public void processSegmentation(String cluster, Map<User, RFMResult> rfmResults, Date[] recencyThresholds, Integer[] frequencyThresholds, Double[] monetaryThresholds) throws Exception {};

    public void finishDataIntepretation() throws Exception {
        dataExporter.finishDataExport();
    }
}
