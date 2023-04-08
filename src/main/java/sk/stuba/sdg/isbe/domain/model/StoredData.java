package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class StoredData {
    @Id
    private String uid;
    private DataPointTag dataPointTag;
    private Double value;
    private Long measureAdd;
    private boolean deactivated;

    private String deviceId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public DataPointTag getDataPointTag() {
        return dataPointTag;
    }

    public void setDataPointTag(DataPointTag dataPointTag) {
        this.dataPointTag = dataPointTag;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Long getMeasureAdd() {
        return measureAdd;
    }

    public void setMeasureAdd(Long measureAdd) {
        this.measureAdd = measureAdd;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isValid() {
        return getDataPointTag() == null && getMeasureAdd() == null && getValue() == null;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
