package sk.stuba.sdg.isbe.domain.model;

public class DataPointTag {
    private String uid;
    private String name;
    private String unit;
    private Long decimal;
    private boolean deactivated;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getDecimal() {
        return decimal;
    }

    public void setDecimal(Long decimal) {
        this.decimal = decimal;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isValid() {
        return this.getName() != null &&
                this.getUnit() != null;
    }
}
