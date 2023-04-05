package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.JobStatus;

public interface JobStatusService {
    JobStatus createJobStatus(JobStatus jobStatus);

    JobStatus getJobStatus(String jobStatusId);

    JobStatus updateJobStatus(String jobStatusId, JobStatus changeJobStatus);

    void validateJobStatus(JobStatus jobStatus);
}
