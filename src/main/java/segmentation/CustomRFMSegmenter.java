package segmentation;

import data.IRFMDataExporter;
import data.IRFMDataProvider;

import java.util.Arrays;
import java.util.Date;

public class CustomRFMSegmenter extends RFMSegmenter {
    public CustomRFMSegmenter(IRFMDataProvider dataProvider, IRFMDataExporter dataExporter, Date[] recencyThresholds, Integer[] frequencyThresholds, Double[] monetaryThresholds) {
        super(dataProvider, dataExporter);
        this.recencyThresholds = recencyThresholds;
        this.frequencyThresholds = frequencyThresholds;
        this.monetaryThresholds = monetaryThresholds;

        Arrays.sort(this.recencyThresholds);
        Arrays.sort(this.frequencyThresholds);
        Arrays.sort(this.monetaryThresholds);
    }
}
