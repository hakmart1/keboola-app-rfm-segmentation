package data;

import segmentation.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class CsvRFMDataProvider implements IRFMDataProvider {
    private String csvFilePath, separator, dateFormat;
    private boolean monetaryAsAvg;
    private Columns columns;

    public CsvRFMDataProvider(String csvFilePath, String separator, Columns columns, String dateFormat, boolean monetaryAsAvg) {
        this.csvFilePath = csvFilePath;
        this.separator = separator;
        this.columns = columns;
        this.dateFormat = dateFormat;
        this.monetaryAsAvg = monetaryAsAvg;
    }

    @Override
    public Map<String, List<User>> getData() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        Map<String, Map<String, User>> result = new HashMap<String,Map<String, User>>();

        String line = br.readLine();
        Indexes indexes = this.getIndexes(line.replaceAll("\"", ""));

        if (indexes.missingFields()){
            throw new Exception("Missing mandatory field. Check the specified column names.");
        }

        while ((line = br.readLine()) != null) {
            String[] data = line.replaceAll("\"", "").split(separator);
            String cluster = indexes.haveCluster() ? data[indexes.getClusterIndex()] : "";
            Date date = new SimpleDateFormat(dateFormat).parse(data[indexes.getDateIndex()]);
            double price = Double.parseDouble(data[indexes.getPriceIndex()]);
            String userId = data[indexes.getEntityIdIndex()];

            result.putIfAbsent(cluster, new HashMap<String, User>());

            User user = result.get(cluster).putIfAbsent(userId, new User(userId, date, 1, price));
            if (user != null) {
                user.setFrequency(user.getFrequency() + 1);
                user.setMonetary(user.getMonetary() + price);
                user.setRecency(user.getRecency().compareTo(date) > 0 ? user.getRecency() : date);
            }
        }

        Map<String, List<User>> rfm = new HashMap<String, List<User>>(result.size());
        for (Map.Entry<String, Map<String, User>> cluster : result.entrySet()){
            rfm.put(cluster.getKey(), new ArrayList<User>(cluster.getValue().values()));
        }

        if (monetaryAsAvg){
            for (List<User> cluster : rfm.values()){
                for (User user : cluster) {
                    user.setMonetary(user.getMonetary() / user.getFrequency());
                }
            }
        }

        return rfm;
    }

    private Indexes getIndexes(String headerLine){
        Indexes result = new Indexes();
        String[] header = headerLine.split(separator);
        for (int i = 0; i < header.length; i++){
            String column = header[i].trim();
            if (column.equals(columns.getCluster())) {
                result.setClusterIndex(i);
            } else if (column.equals(columns.getEntity())) {
                result.setEntityIdIndex(i);
            } else if (column.equals(columns.getPrice())) {
                result.setPriceIndex(i);
            } else if (column.equals(columns.getDate())) {
                result.setDateIndex(i);
            }
        }
        return result;
    }

    private class Indexes {
        private int clusterIndex, entityIdIndex, dateIndex, priceIndex;

        public Indexes() {
            this.clusterIndex = -1;
            this.entityIdIndex = -1;
            this.dateIndex = -1;
            this.priceIndex = -1;
        }

        public boolean haveCluster() {
            return clusterIndex != -1;
        }

        public boolean missingFields(){
            return entityIdIndex == -1 || dateIndex == -1 || priceIndex == -1;
        }

        public int getClusterIndex() {
            return clusterIndex;
        }

        public void setClusterIndex(int clusterIndex) {
            this.clusterIndex = clusterIndex;
        }

        public int getEntityIdIndex() {
            return entityIdIndex;
        }

        public void setEntityIdIndex(int entityIdIndex) {
            this.entityIdIndex = entityIdIndex;
        }

        public int getDateIndex() {
            return dateIndex;
        }

        public void setDateIndex(int dateIndex) {
            this.dateIndex = dateIndex;
        }

        public int getPriceIndex() {
            return priceIndex;
        }

        public void setPriceIndex(int priceIndex) {
            this.priceIndex = priceIndex;
        }
    }
}
