package segmentation;

import data.CsvRFMDataExporter;
import data.CsvRFMDataProvider;
import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class GradualQuantilRFMSegmenterTest {
    @Mock
    public CsvRFMDataProvider csvRFMDataProvider;

    @Mock
    public CsvRFMDataExporter csvRFMDataExporter;

    @Test
    public void largeFirstSegment() throws Exception {
        RFMSegmenter rfmSegmenter = new GradualQuantilRFMSegmenter(csvRFMDataProvider, csvRFMDataExporter,3,3,3);

        rfmSegmenter.resolveThresholds(getTestUsersGroup());
        Assert.assertEquals(2, rfmSegmenter.frequencyThresholds.length);
        Assert.assertArrayEquals(new Integer[] {1,2}, rfmSegmenter.frequencyThresholds);
    }

    @Test
    public void sameValueForEveryUser() throws Exception {
        RFMSegmenter rfmSegmenter = new GradualQuantilRFMSegmenter(csvRFMDataProvider, csvRFMDataExporter,3,3,3);

        rfmSegmenter.resolveThresholds(getTestUsersGroup());
        Assert.assertEquals(2, rfmSegmenter.monetaryThresholds.length);
        Assert.assertArrayEquals(new Double[] {1.0,1.0}, rfmSegmenter.monetaryThresholds);
    }

    @Test
    public void oneSegmentOnly() throws Exception {
        RFMSegmenter rfmSegmenter = new GradualQuantilRFMSegmenter(csvRFMDataProvider, csvRFMDataExporter,1,1,1);

        rfmSegmenter.resolveThresholds(getTestUsersGroup());
        Assert.assertEquals(0, rfmSegmenter.monetaryThresholds.length);
    }

    @Test
    public void fewerUsersThanBoundaries() throws Exception {
        RFMSegmenter rfmSegmenter = new GradualQuantilRFMSegmenter(csvRFMDataProvider, csvRFMDataExporter,3,3,3);

        rfmSegmenter.resolveThresholds(Arrays.asList(new User("TestUserId1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-10").getTime(), 1, 1)));
        Assert.assertEquals(2, rfmSegmenter.monetaryThresholds.length);
        Assert.assertArrayEquals(new Double[]{1.0, 1.0}, rfmSegmenter.monetaryThresholds);
    }

    private static List<User> getTestUsersGroup() throws Exception {
        return Arrays.asList(
                new User("TestUserId1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-01").getTime(), 1, 1),
                new User("TestUserId2", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-02").getTime(), 1, 1),
                new User("TestUserId3", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-03").getTime(), 1, 1),
                new User("TestUserId4", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-04").getTime(), 1, 1),
                new User("TestUserId5", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-05").getTime(), 1, 1),
                new User("TestUserId6", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-06").getTime(), 1, 1),
                new User("TestUserId7", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-07").getTime(), 2, 1),
                new User("TestUserId8", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-08").getTime(), 2, 1),
                new User("TestUserId9", new SimpleDateFormat("yyyy-MM-dd").parse("2018-10-09").getTime(), 3, 1)
        );
    }
}