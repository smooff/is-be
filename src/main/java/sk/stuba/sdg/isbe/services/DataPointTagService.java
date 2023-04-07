package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.DataPoint;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.util.List;

public interface DataPointTagService {

    DataPointTag createDataPointTag(DataPointTag dataPointTag);

    List<DataPointTag> getDataPointTags();

    DataPointTag getDataPointTagById(String dataPointTagId);

    List<StoredData> getStoredData(String dataPointTagId);

    DataPointTag updateDataPointTag(String dataPointTagId, DataPointTag changeDataPointTag);

    DataPointTag deleteDataPointTag(String dataPointTagId);
}
