package segmentation;

public class RFMResult {
    private int recency;
    private int frequency;
    private int monetary;

    public RFMResult(int recency, int frequency, int monetary) {
        this.recency = recency;
        this.frequency = frequency;
        this.monetary = monetary;
    }

    public int getRecency() {
        return recency;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getMonetary() {
        return monetary;
    }
}
