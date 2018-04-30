package segmentation;

import data.ICustomDataExporter;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class FMShiftInterpreter extends RFMSegmentationInterpreter {
    public FMShiftInterpreter(ICustomDataExporter dataExporter) {
        super(dataExporter);
    }

    @Override
    public void processSegmentation(Map<User, RFMResult> rfmResults, Date[] recencyThresholds, Integer[] frequencyThresholds, Double[] monetaryThresholds) throws Exception {
        int freqencyMax = Collections.max(rfmResults.keySet(), new User.FreqencyComparator()).getFrequency();
        double monetaryMax = Collections.max(rfmResults.keySet(), new User.MonetaryComparator()).getMonetary();

        double pesimistic = 0, realistic = 0, optimistic = 0;
        for (Map.Entry<User, RFMResult> entry : rfmResults.entrySet()) {
            User user = entry.getKey();
            RFMResult result = entry.getValue();
            int pF = 0, rF = 0, oF = 0;
            double pM = 0, rM = 0, oM = 0;
            if (result.getFrequency() <= result.getMonetary() && result.getFrequency() <= frequencyThresholds.length) {
                pF = frequencyThresholds[result.getFrequency() - 1] - user.getFrequency();
                oF = (result.getFrequency() < frequencyThresholds.length ? frequencyThresholds[result.getFrequency()] : freqencyMax) - user.getFrequency();
                rF = (pF+oF) / 2;
            } else {
                pF = oF = rF = user.getFrequency();
            }

            if (result.getMonetary() <= result.getFrequency() && result.getMonetary() <= monetaryThresholds.length) {
                pM = monetaryThresholds[result.getMonetary() - 1] - user.getMonetary();
                oM = (result.getMonetary() < monetaryThresholds.length ? monetaryThresholds[result.getMonetary()] : monetaryMax) - user.getMonetary();
                rM = (pM+oM) / 2;
            } else {
                pM = oM = rM = user.getMonetary();
            }
            pesimistic += pF*pM;
            realistic += rF*rM;
            optimistic += oF*oM;
        }

        dataExporter.addDataToOutputContainer(pesimistic+","+realistic+","+optimistic);
    }

    @Override
    public void processSegmentation(String cluster, Map<User, RFMResult> rfmResults, Date[] recencyThresholds, Integer[] frequencyThresholds, Double[] monetaryThresholds) throws Exception {
        dataExporter.addDataToOutputContainer(cluster+",");
        processSegmentation(rfmResults, recencyThresholds, frequencyThresholds, monetaryThresholds);
    }
}
