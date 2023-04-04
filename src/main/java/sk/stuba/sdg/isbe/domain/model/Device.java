package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

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
    private Long addAt;
    private List<String> Jobs;
    private List<JobStatus> JobsStatus;
    private boolean deactivated;

    public List<JobStatus> getJobsStatus() {
        return JobsStatus;
    }

    public void setJobsStatus(List<JobStatus> jobsStatus) {
        JobsStatus = jobsStatus;
    }

    public List<String> getJobs() {
        return Jobs;
    }

    public void setJobs(List<String> jobs) {
        Jobs = jobs;
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

    public Long getAddAt() {
        return addAt;
    }

    public void setAddAt(Long addAt) {
        this.addAt = addAt;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
