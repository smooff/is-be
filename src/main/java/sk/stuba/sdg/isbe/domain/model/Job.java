package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Document
public class Job {
    @Id
    private String uid;
    private String deviceId;
    private String name;
    private Integer noOfCmds;
    private Integer noOfReps;
    @DBRef
    private JobStatus status;
    private JobStatusEnum currentStatus;
    private List<Command> commands;
    private boolean toCancel;
    private boolean paused;
    private Long createdAt;
    private Long startedAt;
    private Long finishedAt;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNoOfCmds() {
        return noOfCmds;
    }

    public void setNoOfCmds(Integer noOfCmds) {
        this.noOfCmds = noOfCmds;
    }

    public Integer getNoOfReps() {
        return noOfReps;
    }

    public void setNoOfReps(Integer noOfReps) {
        this.noOfReps = noOfReps;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobStatusEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(JobStatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public boolean isToCancel() {
        return toCancel;
    }

    public void setToCancel(boolean toCancel) {
        this.toCancel = toCancel;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getFormattedTime(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return formatter.format(date);
    }

    public boolean isValid() {
        return this.getName() != null &&
                this.getCommands() != null &&
                !this.getCommands().isEmpty() &&
                this.getNoOfReps() != null;
    }
}
