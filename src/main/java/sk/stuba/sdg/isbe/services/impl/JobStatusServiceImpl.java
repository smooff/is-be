package sk.stuba.sdg.isbe.services.impl;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.events.DataStoredEvent;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;
import sk.stuba.sdg.isbe.services.StoredDataService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private StoredDataRepository storedDataRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    StoredDataService storedDataService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public JobStatus createJobStatus(JobStatus jobStatus){
        jobStatus.setCreatedAt(Instant.now().toEpochMilli());
        jobStatus.setLastUpdated(LocalDateTime.now());
        return upsertJobStatus(jobStatus);
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
            changeJobsCurrentStatus(jobStatus.getJobId(), changeJobStatus.getRetCode());
        }
        if (changeJobStatus.getCode() != null) {
            jobStatus.setCode(changeJobStatus.getCode());
            changeJobsCurrentStatus(jobStatus.getJobId(), changeJobStatus.getCode());
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
                for (DataPoint dataPoint : jobStatus.getData()) {
                    DataPointTag dataPointTag = dataPointTags.stream().filter(data -> Objects.equals(data.getTag(), dataPoint.getTag())).findAny()
                            .orElse(null);
                    StoredData storedData = new StoredData();
                    storedData.setDataPointTagId(dataPointTag.getUid());
                    storedData.setTag(dataPointTag.getTag());
                    storedData.setValue(dataPoint.getValue());
                    storedData.setMeasureAdd(Instant.now().toEpochMilli());
                    storedData.setDeviceId(deviceId);
                    storedDataService.upsertStoredData(storedData);
                    listStoredData.add(storedData);
                }
                DataStoredEvent dataStoredEvent = new DataStoredEvent(this, listStoredData, deviceId);
                eventPublisher.publishEvent(dataStoredEvent);
            }
        }

        jobStatus.setLastUpdated(LocalDateTime.now());
        return upsertJobStatus(jobStatus);
    }

    private void changeJobsCurrentStatus(String jobId, JobStatusEnum jobStatus) {
        Job job = jobService.getJobById(jobId);
        job.setCurrentStatus(jobStatus);
        jobRepository.save(job);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }

    @Override
    public JobStatus upsertJobStatus(JobStatus jobStatus) {
        Query query = new Query(Criteria.where("uid").is(jobStatus.getUid()));
        Update update = new Update()
                .set("jobId", jobStatus.getJobId())
                .set("retCode", jobStatus.getRetCode())
                .set("code", jobStatus.getCode())
                .set("currentStep", jobStatus.getCurrentStep())
                .set("totalSteps", jobStatus.getTotalSteps())
                .set("currentCycle", jobStatus.getCurrentCycle())
                .set("data", jobStatus.getData())
                .set("createdAt", jobStatus.getCreatedAt())
                .set("lastUpdated", jobStatus.getLastUpdated());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, JobStatus.class);

        if (updateResult.getMatchedCount() == 0) {
            // if no matching document found, insert a new document
            mongoTemplate.insert(jobStatus);
        } else {
            // if a matching document is found, update the scenario object with the latest data
            jobStatus = mongoTemplate.findOne(query, JobStatus.class);
        }

        return jobStatus;
    }
}
