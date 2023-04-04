package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.JobStatus;

public interface JobStatusService {
    JobStatus createJobStatus(JobStatus jobStatus);

    void validateJobStatus(JobStatus jobStatus);
}
