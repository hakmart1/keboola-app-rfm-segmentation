package segmentation;

import java.util.Comparator;
import java.util.Date;

public class User {
    private String userId;
    private Date recency;
    private int frequency;
    private double monetary;

    public User(String userId, Date recency, int frequency, double monetary) {
        this.userId = userId;
        this.recency = recency;
        this.frequency = frequency;
        this.monetary = monetary;
    }

    public String getUserId() {
        return this.userId;
    }

    public Date getRecency() {
        return this.recency;
    }

    public void setRecency(Date recency) {
        this.recency = recency;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public double getMonetary() {
        return this.monetary;
    }

    public void setMonetary(double monetary) {
        this.monetary = monetary;
    }

    static class RecencyComparator implements Comparator<User> {
        @Override
        public int compare(User firstUser, User secondUser) {
            return firstUser.getRecency().compareTo(secondUser.getRecency());
        }
    }

    static class FreqencyComparator implements Comparator<User> {
        @Override
        public int compare(User firstUser, User secondUser) {
            return Integer.compare(firstUser.getFrequency(), secondUser.getFrequency());
        }
    }

    static class MonetaryComparator implements Comparator<User> {
        @Override
        public int compare(User firstUser, User secondUser) {
            return Double.compare(firstUser.getMonetary(), secondUser.getMonetary());
        }
    }
}
