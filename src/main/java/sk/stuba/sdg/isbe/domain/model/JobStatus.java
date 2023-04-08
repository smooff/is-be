package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class JobStatus {
    @Id
    private String uid;
    private String jobId;
    private JobStatusEnum retCode;
    private JobStatusEnum code;
    private Integer currentStep;
    private Integer totalSteps;
    private Integer currentCycle;
    private List<DataPoint> data;
    private Long createdAt;
    private LocalDateTime lastUpdated;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobStatusEnum getRetCode() {
        return retCode;
    }

    public void setRetCode(JobStatusEnum retCode) {
        this.retCode = retCode;
    }

    public JobStatusEnum getCode() {
        return code;
    }

    public void setCode(JobStatusEnum code) {
        this.code = code;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
    }

    public Integer getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(Integer currentCycle) {
        this.currentCycle = currentCycle;
    }

    public List<DataPoint> getData() {
        return data;
    }

    public void setData(List<DataPoint> data) {
        this.data = data;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
