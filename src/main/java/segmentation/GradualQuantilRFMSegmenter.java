package segmentation;

import data.IRFMDataExporter;
import data.IRFMDataProvider;

import java.util.*;

public class GradualQuantilRFMSegmenter extends RFMSegmenter {
    private int recencyQuantilCount, frequencyQuantilCount, monetaryQuantilCount;

    public GradualQuantilRFMSegmenter(IRFMDataProvider dataProvider, IRFMDataExporter dataExporter, int recencyQuantilCount, int frequencyQuantilCount, int monetaryQuantilCount){
        super(dataProvider, dataExporter);
        this.recencyQuantilCount = recencyQuantilCount;
        this.frequencyQuantilCount = frequencyQuantilCount;
        this.monetaryQuantilCount = monetaryQuantilCount;
    }

    @Override
    protected void resolveThresholds(List<User> users) {
        recencyThresholds = new Long[recencyQuantilCount-1];
        frequencyThresholds = new Integer[frequencyQuantilCount-1];
        monetaryThresholds = new Double[monetaryQuantilCount-1];

        Collections.sort(users, new User.RecencyComparator());
        int taken = 0;
        for (int i = 1; i <= this.recencyThresholds.length; i++){
            int index = (int)Math.max(0,((users.size() - taken) / ((double)recencyQuantilCount-i+1))-1+taken);
            recencyThresholds[i-1] = users.get(index).getRecency();
            while (index < users.size() && users.get(index).getRecency() == recencyThresholds[i-1]){
                index++;
            }
            taken = index;
        }

        Collections.sort(users, new User.FrequencyComparator());
        taken = 0;
        for (int i = 1; i <= frequencyThresholds.length; i++){
            int index = (int)Math.max(0,((users.size() - taken) / ((double)frequencyQuantilCount-i+1))-1+taken);
            frequencyThresholds[i-1] = users.get(index).getFrequency();
            while (index < users.size() && users.get(index).getFrequency() == frequencyThresholds[i-1]){
                index++;
            }
            taken = index;
        }

        Collections.sort(users, new User.MonetaryComparator());
        taken = 0;
        for (int i = 1; i <= monetaryThresholds.length; i++){
            int index = (int)Math.max(0,((users.size() - taken) / ((double)monetaryQuantilCount-i+1))-1+taken);
            monetaryThresholds[i-1] = users.get(index).getMonetary();
            while (index < users.size() && users.get(index).getMonetary() == monetaryThresholds[i-1]){
                index++;
            }
            taken = index;
        }
    }
}

