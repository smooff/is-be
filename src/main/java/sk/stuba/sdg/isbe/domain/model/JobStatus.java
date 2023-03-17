package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;

@Document
public class JobStatus {
    @Id
    private String uid;
    private JobStatusEnum retCode;
    private JobStatusEnum code;
    private Integer currentStep;
    private Integer totalSteps;
    private Integer currentCycle;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}