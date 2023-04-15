package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

import java.util.ArrayList;
import java.util.List;

@Document
public class Device {
    @Id
    private String uid;
    private String name;
    private String mac;
    private DeviceTypeEnum type;
    private String version;
    private String firmware;
    @DBRef
    private List<Job> jobs = new ArrayList<>();
    @DBRef
    private List<DataPointTag> dataPointTags = new ArrayList<>();
    private Long responseTime;
    private Long addTime;
    private Long initExpireTime;
    private String initApiKey;
    private boolean deactivated;

    public Device() {}

    public Device(String name, String mac, DeviceTypeEnum type) {
        this.name = name;
        this.mac = mac;
        this.type = type;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

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

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public DeviceTypeEnum getType() {
        return type;
    }

    public void setType(DeviceTypeEnum type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFirmware() {
        return firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public List<DataPointTag> getDataPointTags() {
        return dataPointTags;
    }

    public void setDataPointTags(List<DataPointTag> dataPointTags) {
        this.dataPointTags = dataPointTags;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public Long getInitExpireTime() {
        return initExpireTime;
    }

    public void setInitExpireTime(Long initExpireTime) {
        this.initExpireTime = initExpireTime;
    }

    public String getInitApiKey() {
        return initApiKey;
    }

    public void setInitApiKey(String initApiKey) {
        this.initApiKey = initApiKey;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
