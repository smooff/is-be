package sk.stuba.sdg.isbe.domain.model;

public class DataPoint {
    private String tag;
    private Double value;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
