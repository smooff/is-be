package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.DataPointSave;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;

import java.util.List;

public interface DataPointSaveService {
    DataPointSave createDataPointSave(DataPointSave dataPointSave);

    List<DataPointSave> getDataPointSaves();

    DataPointSave getDataPointSaveById(String dataPointSaveId);

    DataPointSave updateDataPointSave(String dataPointSaveId, DataPointSave changeDataSaveTag);

    DataPointSave deleteDataPointSave(String dataPointSaveId);
}
