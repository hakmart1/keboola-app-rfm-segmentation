package presentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import data.*;
import segmentation.*;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class KeboolaRFMSegmentation {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Path to config must be specified");
            System.exit(1);
        }
        JsonObject config = null;

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(args[0]));
            JsonParser jsonParser = new JsonParser();

            config = jsonParser.parse(jsonReader).getAsJsonObject();
            jsonReader.close();
        } catch (Exception ex) {
            System.err.println("System could not parse the config file properly.\n" + ex.getMessage());
            System.exit(1);
        }

        if (config.getAsJsonObject("storage").getAsJsonObject("input").getAsJsonArray("tables").get(0).getAsJsonObject().getAsJsonPrimitive("destination") == null) {
            System.err.println("Input table must be specified");
            System.exit(1);
        }
        String inputTable = config.getAsJsonObject("storage").getAsJsonObject("input").getAsJsonArray("tables").get(0).getAsJsonObject().getAsJsonPrimitive("destination").getAsString();

        JsonArray outputs = config.getAsJsonObject("storage").getAsJsonObject("output").getAsJsonArray("tables");
        if (outputs.get(0).getAsJsonObject().getAsJsonPrimitive("source") == null) {
            System.err.println("Output file must be specified");
            System.exit(1);
        }
        String outputBucket = outputs.get(0).getAsJsonObject().getAsJsonPrimitive("source").getAsString();

        config = config.getAsJsonObject("parameters");

        if (config.getAsJsonPrimitive("method") == null) {
            System.err.println("Segmentation method must be specified");
            System.exit(1);
        }
        String method = config.getAsJsonPrimitive("method").getAsString();

        if (config.getAsJsonPrimitive("dateFormat") == null) {
            System.err.println("Format of date must be specified");
            System.exit(1);
        }
        String dateFormat = config.getAsJsonPrimitive("dateFormat").getAsString();

        if (config.getAsJsonObject("columns") == null) {
            System.err.println("Columns must be specified");
            System.exit(1);
        }
        JsonObject columns = config.getAsJsonObject("columns");

        if (columns.getAsJsonPrimitive("entity") == null) {
            System.err.println("Entity column must be specified");
            System.exit(1);
        }
        String entity = columns.getAsJsonPrimitive("entity").getAsString();

        if (columns.getAsJsonPrimitive("price") == null) {
            System.err.println("Price column must be specified");
            System.exit(1);
        }
        String price = columns.getAsJsonPrimitive("price").getAsString();

        if (columns.getAsJsonPrimitive("date") == null) {
            System.err.println("Date column must be specified");
            System.exit(1);
        }
        String date = columns.getAsJsonPrimitive("date").getAsString();

        String cluster = "";
        if (columns.getAsJsonPrimitive("cluster") != null) {
            cluster = columns.getAsJsonPrimitive("cluster").getAsString();
        }
        boolean isClustered = cluster != null && !cluster.isEmpty();

        IRFMSegmenter rfmSegmenter = null;
        String separator = ",";

        Columns columnsHeaders = new Columns(isClustered ? cluster : "", entity, price, date);

        IRFMDataProvider dataProvider = new CsvRFMDataProvider("/data/in/tables/"+inputTable, separator, columnsHeaders, dateFormat, true);
        IRFMDataExporter dataExporter = new CsvRFMDataExporter("/data/out/tables/"+outputBucket, separator, isClustered, dateFormat);

        if (method.equals("quantil") || method.equals("gradualQuantil")) {
            if (config.getAsJsonPrimitive("recencySegmentsCount") == null) {
                System.err.println("In case of Quantil method recency segments count must be specified");
                System.exit(1);
            }
            int recencySegmentsCount = config.getAsJsonPrimitive("recencySegmentsCount").getAsInt();

            if (config.getAsJsonPrimitive("frequencySegmentsCount") == null) {
                System.err.println("In case of Quantil method frequency segments count must be specified");
                System.exit(1);
            }
            int frequencySegmentsCount = config.getAsJsonPrimitive("frequencySegmentsCount").getAsInt();

            if (config.getAsJsonPrimitive("monetarySegmentsCount") == null) {
                System.err.println("In case of Quantil method monetary segments count must be specified");
                System.exit(1);
            }
            int monetarySegmentsCount = config.getAsJsonPrimitive("monetarySegmentsCount").getAsInt();

            if (method.equals("quantil")) {
                rfmSegmenter = new QuantilRFMSegmenter(dataProvider, dataExporter, recencySegmentsCount, frequencySegmentsCount, monetarySegmentsCount);
            } else {
                rfmSegmenter = new GradualQuantilRFMSegmenter(dataProvider, dataExporter, recencySegmentsCount, frequencySegmentsCount, monetarySegmentsCount);
            }
        } else if (method.equals("custom")){
            JsonObject thresholds = config.getAsJsonObject("thresholds");
            if (config.getAsJsonObject("thresholds") == null) {
                System.err.println("In case of Custom method thresholds must be specified");
                System.exit(1);
            }

            JsonArray recencyJsonArray = thresholds.getAsJsonArray("recency");
            if (recencyJsonArray == null) {
                System.err.println("In case of Custom method recency thresholds array must be specified");
                System.exit(1);
            }
            Date[] recencyThresholds = new Date[recencyJsonArray.size()];
            try {
                for (int i = 0; i < recencyJsonArray.size(); i++) {
                    recencyThresholds[i] = new SimpleDateFormat(dateFormat).parse(recencyJsonArray.get(i).getAsString());
                }
            } catch (Exception ex) {
                System.err.println("The date has wrong format.\n" + ex.getMessage());
                System.exit(1);
            }

            JsonArray frequencyJsonArray = thresholds.getAsJsonArray("frequency");
            if (frequencyJsonArray == null) {
                System.err.println("In case of Custom method frequency thresholds array must be specified");
                System.exit(1);
            }
            Integer[] frequencyThresholds = new Integer[frequencyJsonArray.size()];
            for (int i = 0; i < frequencyJsonArray.size(); i++) {
                frequencyThresholds[i] = frequencyJsonArray.get(i).getAsInt();
            }

            JsonArray monetaryJsonArray = thresholds.getAsJsonArray("monetary");
            if (monetaryJsonArray == null) {
                System.err.println("In case of Custom method monetary thresholds array must be specified");
                System.exit(1);
            }
            Double[] monetaryThresholds = new Double[monetaryJsonArray.size()];
            for (int i = 0; i < monetaryJsonArray.size(); i++) {
                monetaryThresholds[i] = monetaryJsonArray.get(i).getAsDouble();
            }
            rfmSegmenter = new CustomRFMSegmenter(dataProvider, dataExporter, recencyThresholds, frequencyThresholds, monetaryThresholds);
        } else {
            System.err.println("Unknown segmentation method");
            System.exit(1);
        }


        List<RFMSegmentationInterpreter> interpreters = new ArrayList<RFMSegmentationInterpreter>();

        if (config.getAsJsonPrimitive("potential") != null)
            if (config.getAsJsonPrimitive("potential").getAsString().equals("Yes")) {
            if (outputs.get(1).getAsJsonObject().getAsJsonPrimitive("source") == null) {
                System.err.println("Output file for business potential must be specified");
                System.exit(1);
            }
            interpreters.add(new FMShiftInterpreter(new FileCustomDataExporter("/data/out/tables/"+outputs.get(1).getAsJsonObject().getAsJsonPrimitive("source").getAsString())));
        }

        System.out.println("Parameters parsed successfully.");


        try {
            rfmSegmenter.performRFMSegmentation(isClustered, interpreters);
        } catch (Exception ex){
            System.err.println("During the process of segmentation error occurred.\n" + ex.getMessage() + "\n" + ex.getStackTrace());
            System.exit(1);
        }

        System.out.println("Segmentation successful done.");
    }
}
