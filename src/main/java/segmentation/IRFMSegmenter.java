package segmentation;

import java.util.List;

public interface IRFMSegmenter {
    public void performRFMSegmentation(boolean isClustered, List<RFMSegmentationInterpreter> interpreters) throws Exception;
}
