package data;

public class Columns {
    private String entity = "", price = "", date = "", cluster = "";

    public Columns(String cluster, String entity, String price, String date) {
        this.entity = entity.trim();
        this.price = price.trim();
        this.date = date.trim();
        this.cluster = cluster.trim();
    }

    public String getEntity() {
        return entity;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    public String getCluster() {
        return cluster;
    }
}
