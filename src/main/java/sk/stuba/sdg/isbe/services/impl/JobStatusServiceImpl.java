package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.events.DataStoredEvent;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;

import java.time.Instant;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    DeviceService deviceService;

    @Autowired
    StoredDataRepository storedDataRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    JobService jobService;

    @Override
    public JobStatus createJobStatus(JobStatus jobStatus){
        jobStatus.setCreatedAt(Instant.now().toEpochMilli());
        jobStatus.setLastUpdated(LocalDateTime.now());
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
    public JobStatus updateJobStatus(String jobStatusId, JobStatus changeJobStatus, String deviceId) {
        JobStatus jobStatus = getJobStatus(jobStatusId);

        if (changeJobStatus == null) {
            throw new InvalidEntityException("JobStatus with changes is null!");
        }

        if (changeJobStatus.getRetCode() != null) {
            jobStatus.setRetCode(changeJobStatus.getRetCode());
        }
        if (changeJobStatus.getCode() != null) {
            jobStatus.setCode(changeJobStatus.getCode());
            jobService.getJob(jobStatus.getJobId()).setCurrentStatus(changeJobStatus.getCode());
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

            if (deviceId != null) {
                List<DataPointTag> dataPointTags = deviceService.getDeviceById(deviceId).getDataPointTags();
                 List<StoredData> listStoredData = new ArrayList<>();
                for(DataPoint dataPoint :jobStatus.getData()) {
                    DataPointTag dataPointTag = dataPointTags.stream().filter(data -> Objects.equals(data.getTag(), dataPoint.getTag())).findAny()
                            .orElse(null);
                    StoredData storedData = new StoredData();
                    storedData.setDataPointTagId(dataPointTag.getUid());
                    storedData.setTag(dataPointTag.getTag());
                    storedData.setValue(dataPoint.getValue());
                    storedData.setMeasureAdd(Instant.now().toEpochMilli());
                    storedData.setDeviceId(deviceId);
                    storedDataRepository.save(storedData);
                    listStoredData.add(storedData);
                }
                DataStoredEvent dataStoredEvent = new DataStoredEvent(this, listStoredData, deviceId);
                eventPublisher.publishEvent(dataStoredEvent);
            }
        }

        jobStatus.setLastUpdated(LocalDateTime.now());
        return jobStatusRepository.save(jobStatus);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }
}
