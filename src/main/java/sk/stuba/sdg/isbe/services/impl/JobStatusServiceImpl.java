package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.*;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DataPointSaveRepository;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobStatusService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobStatusServiceImpl implements JobStatusService {

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    DeviceService deviceService;

    @Autowired
    DataPointSaveRepository dataPointSaveRepository;

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
                for(DataPoint dataPoint :jobStatus.getData()) {
                    DataPointTag dataPointTag = dataPointTags.stream().filter(data -> Objects.equals(data.getTag(), dataPoint.getTag())).findAny()
                            .orElse(null);
                    DataPointSave dataPointSave = new DataPointSave();
                    dataPointSave.setDataPointTag(dataPointTag);
                    dataPointSave.setValue(dataPoint.getValue());
                    dataPointSave.setMeasureAdd(Instant.now().toEpochMilli());
                    dataPointSaveRepository.save(dataPointSave);
                }
            }
        }
        
        return jobStatusRepository.save(jobStatus);
    }

    @Override
    public void validateJobStatus(JobStatus jobStatus) {

    }
}
