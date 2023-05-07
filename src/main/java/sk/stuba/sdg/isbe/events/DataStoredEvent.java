package sk.stuba.sdg.isbe.events;

import org.springframework.context.ApplicationEvent;
import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.time.Instant;
import java.util.List;

public class DataStoredEvent extends ApplicationEvent {
    private final List<StoredData> storedData;

    private final String deviceId;

    private final Instant time;

    public DataStoredEvent(Object source, List<StoredData> storedData, String deviceId, Instant time) {
        super(source);
        this.storedData = storedData;
        this.deviceId = deviceId;
        this.time = time;
    }

    public List<StoredData> getStoredData() {
        return storedData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Instant getTime() {
        return time;
    }
}