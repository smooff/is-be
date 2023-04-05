package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.DataPointSave;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.DataPointSaveRepository;
import sk.stuba.sdg.isbe.services.DataPointSaveService;

import java.time.Instant;
import java.util.List;

@Service
public class DataPointSaveServiceImpl implements DataPointSaveService {

    @Autowired
    private DataPointSaveRepository dataPointSaveRepository;

    @Override
    public DataPointSave createDataPointSave(DataPointSave dataPointSave){
        if (!dataPointSave.isValid()) {
            throw new InvalidEntityException("Data Point Save has no name or unit set!");
        }

        dataPointSave.setMeasureAdd(Instant.now().toEpochMilli());
        return dataPointSaveRepository.save(dataPointSave);
    }

    @Override
    public List<DataPointSave> getDataPointSaves() {
        return dataPointSaveRepository.findAll();
    }

    @Override
    public DataPointSave getDataPointSaveById(String dataPointSaveId) {
        if (dataPointSaveId == null || dataPointSaveId.isEmpty()) {
            throw new InvalidEntityException("Data Point Save id is not set!");
        }

        return dataPointSaveRepository.getDataPointSaveByUid(dataPointSaveId);
    }

    @Override
    public DataPointSave updateDataPointSave(String dataPointSaveId, DataPointSave changeDataPointSave) {
        DataPointSave dataPointSave = getDataPointSaveById(dataPointSaveId);

        if (changeDataPointSave == null) {
            throw new InvalidEntityException("DataPointSave with changes is null!");
        }

        if (changeDataPointSave.getDataPointTag() != null) {
            dataPointSave.setDataPointTag(changeDataPointSave.getDataPointTag());
        }
        if (changeDataPointSave.getValue() != null) {
            dataPointSave.setValue(changeDataPointSave.getValue());
        }

        return dataPointSaveRepository.save(dataPointSave);
    }

    @Override
    public DataPointSave deleteDataPointSave(String dataPointSaveId) {
        DataPointSave dataPointSave = getDataPointSaveById(dataPointSaveId);
        dataPointSave.setDeactivated(true);
        dataPointSaveRepository.save(dataPointSave);
        return dataPointSave;
    }
}
