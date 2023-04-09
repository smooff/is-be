package sk.stuba.sdg.isbe.events;

import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationProcessor {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StoredDataRepository storedDataRepository;

    JsonLogic jsonLogic = new JsonLogic();

    @Async
    @EventListener
    public void handleDataSavedEvent(DataStoredEvent event) throws JsonLogicException {
        List<Notification> notifications = notificationRepository.getNotificationByDevicesContaining(event.getDeviceId());
        if (notifications != null) {
            List<String> alreadyEvaluated = new ArrayList<>();
            for (StoredData storedData : event.getStoredData()) {  //z1.teplota, z1.vlhkost, z1.svetlo
                Map<String, Double> dataForExpression = new HashMap<>();
                for (Notification notification : notifications) {  //N1: z1.teplota, z1.vlhkost, z1.svetlo   - N2: z1.teplota
                    if (!alreadyEvaluated.contains(notification.getId())) {
                        Map<String, List<String>> mapDeviceAndTag = notification.getDeviceAndTag();
                        mapDeviceAndTag.get(event.getDeviceId()).remove(storedData.getTag());
                        dataForExpression.put(storedData.getDeviceId() + storedData.getTag(), storedData.getValue());
                        mapDeviceAndTag.forEach((k, v) -> {
                            for (String tag : v) {
                                StoredData lastStoredData = storedDataRepository.findFirstStoredDataByDeviceIdAndTagOrderByMeasureAddDesc(k, tag);
                                if (lastStoredData != null) {
                                    dataForExpression.put(lastStoredData.getDeviceId() + lastStoredData.getTag(), lastStoredData.getValue());
                                }
                            }
                        });
                        /**
                         * TODO - notification evaluation
                         */
                        System.out.println(dataForExpression);
                        System.out.println(notification.getRules());
                        // desatinne cisla musia byt pisane s . -> 4.1

                        String result = (String) jsonLogic.apply(notification.getRules(), dataForExpression);
                        switch (result) {
                            case EventConstants.NO_ACTION -> System.out.println("no action");
                            case EventConstants.ACTION -> System.out.println("some action");
                            default -> {
                            }
                        }

                        System.out.println(result);

                        dataForExpression.clear();
                        alreadyEvaluated.add(notification.getId());
                    }
                }
            }
        }
    }
}

//{
//        "retCode": "JOB_FREE",
//        "code": "JOB_FREE",
//        "jobId": "643040271b6f0305d03b258d",
//        "currentStep": 0,
//        "totalSteps": 0,
//        "currentCycle": 0,
//        "data": [
//        {
//        "tag": "teplota",
//        "value": 1
//        },
//        {
//        "tag": "vlhkost",
//        "value": 0
//        },
//        {
//        "tag": "svetlo",
//        "value": 4
//        }
//        ]
//        }