package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Job {
    @Id
    private String uid;
    private String name;
    private Integer noOfCmds;
    private Integer noOfReps;
    private JobStatus status;
    private List<Command> commands;
    private boolean toCancel;
    private boolean paused;

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
}
