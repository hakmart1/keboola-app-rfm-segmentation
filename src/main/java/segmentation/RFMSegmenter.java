package segmentation;

import data.IRFMDataExporter;
import data.IRFMDataProvider;

import java.util.*;

public abstract class RFMSegmenter implements IRFMSegmenter{
    private IRFMDataProvider dataProvider;
    private IRFMDataExporter dataExporter;
    protected Long[] recencyThresholds;
    protected Integer[] frequencyThresholds;
    protected Double[] monetaryThresholds;

    RFMSegmenter(IRFMDataProvider dataProvider, IRFMDataExporter dataExporter){
        this.dataProvider = dataProvider;
        this.dataExporter = dataExporter;
    }

    private <T extends Comparable> int getSegment(T value, T[] thresholds){
        for (int i = 0; i < thresholds.length; i++) {
            if (value.compareTo(thresholds[i]) <= 0) {
                return i + 1;
            }
        }
        return thresholds.length + 1;
    }

    protected void resolveThresholds(List<User> users){}

    private Map<User, RFMResult> performRFMSegmentationOfCluster(List<User> users) {
        resolveThresholds(users);
        Map<User, RFMResult> result = new HashMap<User, RFMResult>(users.size());
        for (User user: users){
            RFMResult rfm = new RFMResult(
                    getSegment(user.getRecency(), recencyThresholds),
                    getSegment(user.getFrequency(), frequencyThresholds),
                    getSegment(user.getMonetary(), monetaryThresholds));
            result.put(user, rfm);
        }
        return result;
    }

    @Override
    public void performRFMSegmentation(boolean isClustered, List<RFMSegmentationInterpreter> interpreters) throws Exception {
        Map<String, List<User>> data = dataProvider.getData();
        System.out.println("Data successfully loaded.");
        dataExporter.createOutputContainer();
        for (RFMSegmentationInterpreter interpreter : interpreters) {
            interpreter.prepareInterpretation(isClustered);
        }
        System.out.println("Output containers successfully created.");
        for(Map.Entry<String, List<User>> clusterData : data.entrySet()) {
            Map<User, RFMResult> rfmResults = performRFMSegmentationOfCluster(clusterData.getValue());
            for (Map.Entry<User, RFMResult> rfmResult : rfmResults.entrySet())
            {
                if (isClustered) {
                    dataExporter.addDataToOutputContainer(clusterData.getKey(), rfmResult.getKey(), rfmResult.getValue());
                } else {
                    dataExporter.addDataToOutputContainer(rfmResult.getKey(), rfmResult.getValue());
                }
            }
            for (RFMSegmentationInterpreter interpreter : interpreters) {
                if (isClustered) {
                    interpreter.processSegmentation(clusterData.getKey(), rfmResults,recencyThresholds,frequencyThresholds,monetaryThresholds);
                } else {
                    interpreter.processSegmentation(rfmResults, recencyThresholds, frequencyThresholds,monetaryThresholds);
                }
            }
        }
        for (RFMSegmentationInterpreter interpreter : interpreters) {
            interpreter.finishDataIntepretation();
        }
        dataExporter.finishDataExport();
    }
}
