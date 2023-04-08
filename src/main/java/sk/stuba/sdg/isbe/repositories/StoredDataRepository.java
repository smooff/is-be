package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.model.StoredData;

public interface StoredDataRepository extends MongoRepository<StoredData, String> {
    StoredData getStoredDataByUid(String StoredDataId);

    StoredData findFirstStoredDataByDeviceIdAndTagOrderByMeasureAddDesc(String deviceId, String tag);
}
