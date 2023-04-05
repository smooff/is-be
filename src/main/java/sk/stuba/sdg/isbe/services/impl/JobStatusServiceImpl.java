package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.services.JobStatusService;

import java.time.Instant;
import java.util.Optional;

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
    public JobStatus getJobStatus(String jobStatusId) {
        Optional<JobStatus> optionalStatus = jobStatusRepository.findById(jobStatusId);
        if (optionalStatus.isEmpty()) {
            throw new NotFoundCustomException("Job-status with ID: '" + jobStatusId + "' was not found!");
        }
        return optionalStatus.get();
    }

    @Override
    public JobStatus updateJobStatus(String jobStatusId, JobStatus changeJobStatus) {
        JobStatus jobStatus = getJobStatus(jobStatusId);

        if (changeJobStatus == null) {
            throw new InvalidEntityException("JobStatus with changes is null!");
        }

        if (changeJobStatus.getRetCode() != null) {
            jobStatus.setRetCode(changeJobStatus.getRetCode());
        }
        if (changeJobStatus.getCode() != null) {
            jobStatus.setCode(changeJobStatus.getCode());
        }
        if (changeJobStatus.getCurrentStep() != null) {
            jobStatus.setCurrentCycle(changeJobStatus.getCurrentStep());
        }
        if (changeJobStatus.getTotalSteps() != null) {
            jobStatus.setTotalSteps(changeJobStatus.getTotalSteps());
        }
        if (changeJobStatus.getCurrentCycle() != null) {
            jobStatus.setCurrentCycle(changeJobStatus.getCurrentCycle());
        }
        if (changeJobStatus.getData() != null) {
            jobStatus.setData(changeJobStatus.getData());
        }
        
        return jobStatusRepository.save(jobStatus);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }
}
