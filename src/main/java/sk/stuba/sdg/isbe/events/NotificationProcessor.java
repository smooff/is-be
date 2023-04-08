package sk.stuba.sdg.isbe.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationProcessor {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StoredDataRepository storedDataRepository;

    @Async
    @EventListener
    public void handleDataSavedEvent(DataStoredEvent event) {
        List<Notification> notifications = notificationRepository.getNotificationByDevicesContaining(event.getDeviceId());
        if (notifications != null) {
            for (StoredData storedData : event.getStoredData()) {
                Map<String, Double> dataForExpression = new HashMap<>();
                for (Notification notification : notifications) {
                    Map<String, String> mapDeviceAndTag = notification.getDeviceAndTag();
                    mapDeviceAndTag.remove(event.getDeviceId());
                    dataForExpression.put(storedData.getDeviceId() + "." + storedData.getDataPointTag().getTag(), storedData.getValue());
                    mapDeviceAndTag.forEach((k, v) -> {
                        StoredData lastStoredData = storedDataRepository.findFirstStoredDataByDeviceIdAndDataPointTag_TagOrderByMeasureAddDesc(k, v);
                        if (lastStoredData != null) {
                            dataForExpression.put(lastStoredData.getDeviceId() + "." + lastStoredData.getDataPointTag().getTag(), lastStoredData.getValue());
                        }
                    });
                    /**
                     * TODO - notification evaluation
                     */
                    System.out.println(dataForExpression);
                    dataForExpression.clear();
                }
            }
        }
    }
}
