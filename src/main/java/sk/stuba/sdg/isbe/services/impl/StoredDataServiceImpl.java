package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.StoredDataService;

import java.time.Instant;
import java.util.List;

@Service
public class StoredDataServiceImpl implements StoredDataService {

    @Autowired
    private StoredDataRepository storedDataRepository;

    @Override
    public StoredData createStoredData(StoredData storedData){
        if (!storedData.isValid()) {
            throw new InvalidEntityException("Data Point Save has no name or unit set!");
        }

        storedData.setMeasureAdd(Instant.now().toEpochMilli());
        return storedDataRepository.save(storedData);
    }

    @Override
    public List<StoredData> getStoredDatas() {
        return storedDataRepository.findAll();
    }

    @Override
    public StoredData getStoredDataById(String storedDataId) {
        if (storedDataId == null || storedDataId.isEmpty()) {
            throw new InvalidEntityException("Data Point Save id is not set!");
        }

        return storedDataRepository.getStoredDataByUid(storedDataId);
    }

    @Override
    public StoredData updateStoredData(String storedDataId, StoredData changeStoredData) {
        StoredData storedData = getStoredDataById(storedDataId);

        if (changeStoredData == null) {
            throw new InvalidEntityException("StoredData with changes is null!");
        }

        if (changeStoredData.getDataPointTagId() != null) {
            storedData.setDataPointTagId(changeStoredData.getDataPointTagId());
        }

        if (changeStoredData.getTag() != null){
            storedData.setTag(changeStoredData.getTag());
        }

        if (changeStoredData.getValue() != null) {
            storedData.setValue(changeStoredData.getValue());
        }

        return storedDataRepository.save(storedData);
    }

    @Override
    public StoredData deleteStoredData(String storedDataId) {
        StoredData storedData = getStoredDataById(storedDataId);
        storedData.setDeactivated(true);
        storedDataRepository.save(storedData);
        return storedData;
    }
}
