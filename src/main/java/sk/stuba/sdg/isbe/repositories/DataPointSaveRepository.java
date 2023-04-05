package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.DataPointSave;

public interface DataPointSaveRepository extends MongoRepository<DataPointSave, String> {
    DataPointSave getDataPointSaveByUid(String dataPointSaveId);
}
