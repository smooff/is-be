package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.DataPointTagRepository;
import sk.stuba.sdg.isbe.services.DataPointTagService;

import java.time.Instant;
import java.util.List;

@Service
public class DataPointTagServiceImpl implements DataPointTagService {

    @Autowired
    private DataPointTagRepository dataPointTagRepository;

    @Override
    public DataPointTag createDataPointTag(DataPointTag dataPointTag){
        if (!dataPointTag.isValid()) {
            throw new InvalidEntityException("Data Point Tag has no name or unit set!");
        }

        dataPointTag.setCreatedAt(Instant.now().toEpochMilli());
        return dataPointTagRepository.save(dataPointTag);
    }

    @Override
    public List<DataPointTag> getDataPointTags() {
        return dataPointTagRepository.findAll();
    }

    @Override
    public DataPointTag getDataPointTagById(String dataPointTagId) {
        if (dataPointTagId == null || dataPointTagId.isEmpty()) {
            throw new InvalidEntityException("Data Point Tag id is not set!");
        }

        return dataPointTagRepository.getDataPointTagByUid(dataPointTagId);
    }

    @Override
    public List<StoredData> getStoredData(String dataPointTagId) {
        if (dataPointTagId == null || dataPointTagId.isEmpty()) {
            throw new InvalidEntityException("Data Point Tag id is not set!");
        }

        return getDataPointTagById(dataPointTagId).getStoredData();
    }

    @Override
    public DataPointTag updateDataPointTag(String dataPointTagId, DataPointTag changeDataPointTag) {
        DataPointTag dataPointTag = getDataPointTagById(dataPointTagId);

        if (changeDataPointTag == null) {
            throw new InvalidEntityException("DataPointTag with changes is null!");
        }

        if (changeDataPointTag.getName() != null) {
            dataPointTag.setName(changeDataPointTag.getName());
        }
        if (changeDataPointTag.getUnit() != null) {
            dataPointTag.setUnit(changeDataPointTag.getUnit());
        }
        if (changeDataPointTag.getDecimal() != null){
            dataPointTag.setDecimal(changeDataPointTag.getDecimal());
        }
        if (changeDataPointTag.getTag() != null){
            dataPointTag.setTag(changeDataPointTag.getTag());
        }

        /**
         * TODO - update vsetkych StoredData, ktore maju v sebe tento DataPointTag
         * dôvod: odstranil sa @DBRef na DataPointTag v triede StoredData
         */

        return dataPointTagRepository.save(dataPointTag);
    }

    @Override
    public DataPointTag deleteDataPointTag(String dataPointTagId) {
        DataPointTag dataPointTag = getDataPointTagById(dataPointTagId);
        dataPointTag.setDeactivated(true);

        /**
         * TODO - update vsetkych StoredData, ktore maju v sebe tento DataPointTag
         * dôvod: odstranil sa @DBRef na DataPointTag v triede StoredData
         */

        dataPointTagRepository.save(dataPointTag);
        return dataPointTag;
    }
}
