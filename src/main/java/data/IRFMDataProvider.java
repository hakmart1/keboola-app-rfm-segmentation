package data;

import segmentation.User;

import java.util.List;
import java.util.Map;

public interface IRFMDataProvider {
    public Map<String, List<User>> getData() throws Exception;
}
