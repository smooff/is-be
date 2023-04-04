package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.DataPoint;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;

import java.util.List;

public interface DataPointTagService {

    DataPointTag createDataPointTag(DataPointTag dataPointTag);

    List<DataPointTag> getDataPointTags();

    DataPointTag getDataPointTagById(String dataPointTagId);

    DataPointTag updateDataPointTag(String dataPointTagId, DataPointTag changeDataPointTag);
}
