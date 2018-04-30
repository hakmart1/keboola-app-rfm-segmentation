package data;

import segmentation.RFMResult;
import segmentation.User;

public interface IRFMDataExporter extends IDataExporter{
    public void addDataToOutputContainer(User user, RFMResult rfmResult) throws Exception;
    public void addDataToOutputContainer(String cluster, User user, RFMResult rfmResult) throws Exception;
}
