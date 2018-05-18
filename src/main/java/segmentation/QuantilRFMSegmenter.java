package segmentation;

import data.IRFMDataExporter;
import data.IRFMDataProvider;

import java.util.*;

public class QuantilRFMSegmenter extends RFMSegmenter {
    private int recencyQuantilCount, frequencyQuantilCount, monetaryQuantilCount;

    public QuantilRFMSegmenter(IRFMDataProvider dataProvider, IRFMDataExporter dataExporter, int recencyQuantilCount, int frequencyQuantilCount, int monetaryQuantilCount){
        super(dataProvider, dataExporter);
        this.recencyQuantilCount = recencyQuantilCount;
        this.frequencyQuantilCount = frequencyQuantilCount;
        this.monetaryQuantilCount = monetaryQuantilCount;
    }

    @Override
    protected void resolveThresholds(List<User> users) {
        recencyThresholds = new Long[this.recencyQuantilCount-1];
        frequencyThresholds = new Integer[this.frequencyQuantilCount-1];
        monetaryThresholds = new Double[this.monetaryQuantilCount-1];

        Collections.sort(users, new User.RecencyComparator());
        double step = users.size() / (double)recencyQuantilCount;
        for (int i = 1; i <= recencyThresholds.length; i++) {
            recencyThresholds[i-1] = users.get((int)Math.max(0,(step*i)-1)).getRecency();
        }
        step = users.size() / (double)frequencyQuantilCount;
        Collections.sort(users, new User.FrequencyComparator());
        for (int i = 1; i <= frequencyThresholds.length; i++) {
            frequencyThresholds[i-1] = users.get((int)Math.max(0,(step*i)-1)).getFrequency();
        }
        step = users.size() / (double)monetaryQuantilCount;
        Collections.sort(users, new User.MonetaryComparator());
        for (int i = 1; i <= monetaryThresholds.length; i++) {
            monetaryThresholds[i-1] = users.get((int)Math.max(0,(step*i)-1)).getMonetary();
        }
    }
}

