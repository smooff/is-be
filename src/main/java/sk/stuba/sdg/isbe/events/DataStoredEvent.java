package sk.stuba.sdg.isbe.events;

import org.springframework.context.ApplicationEvent;
import sk.stuba.sdg.isbe.domain.model.StoredData;

import java.time.Instant;
import java.util.List;

public class DataStoredEvent extends ApplicationEvent {
    private final List<StoredData> storedData;

    private final String deviceId;

    private final String time;

    private final int currStep;

    public DataStoredEvent(Object source, List<StoredData> storedData, String deviceId, String time, int currStep) {
        super(source);
        this.storedData = storedData;
        this.deviceId = deviceId;
        this.time = time;
        this.currStep = currStep;
    }

    public int getCurrStep() {
        return currStep;
    }

    public List<StoredData> getStoredData() {
        return storedData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getTime() {
        return time;
    }
}