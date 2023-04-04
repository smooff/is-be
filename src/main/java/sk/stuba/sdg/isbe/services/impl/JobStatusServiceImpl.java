package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.services.JobStatusService;

import java.time.Instant;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Override
    public JobStatus createJobStatus(JobStatus jobStatus){
        jobStatus.setCreatedAt(Instant.now().toEpochMilli());
        return jobStatusRepository.save(jobStatus);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }
}
