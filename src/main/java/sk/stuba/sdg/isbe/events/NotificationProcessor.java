package sk.stuba.sdg.isbe.events;

import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.NotificationService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificationProcessor {

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StoredDataRepository storedDataRepository;

    JsonLogic jsonLogic = new JsonLogic();

    @Async
    @EventListener
    public void handleDataSavedEvent(DataStoredEvent event) throws JsonLogicException {
        List<Notification> notifications = notificationRepository.getNotificationByDevicesContainingAndDeactivated(event.getDeviceId(), false);
        if (notifications != null) {
            List<String> alreadyEvaluated = new ArrayList<>();
            for (StoredData storedData : event.getStoredData()) {  //z1.teplota, z1.vlhkost, z1.svetlo
                Map<String, Double> dataForExpression = new HashMap<>();
                for (Notification notification : notifications) {  //N1: z1.teplota, z1.vlhkost, z1.svetlo   - N2: z1.teplota
                    if (notification.getMutedUntil() == null || (Instant.now().toEpochMilli() > notification.getMutedUntil())) {
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
                            System.out.println("-----"+dataForExpression+"-----");
//                            System.out.println(notification.getRules());
                            // desatinne cisla musia byt pisane s . -> 4.1

                            String result = (String) jsonLogic.apply(notification.getRules(), dataForExpression);
                            if(notification.getMutedUntil()!= null){
                                notification.setMutedUntil(null);
                            }
                            if (result.contains(EventConstants.NO_ACTION)) {
                                System.out.println("no action");
                            } else if (result.contains(EventConstants.FOR_TIME)){
                                handleNotificationForTime(notification, result);
                            } else if (result.contains(EventConstants.NOTIFICATION_MESSAGE)) {
                                handleNotificationMessage(notification, result);
                            } else{
                                System.out.println("return2");
                            }
                            dataForExpression.clear();
                            alreadyEvaluated.add(notification.getId());
                        }
                    }
                }
            }
        }
    }

    public Long calculateUntilTime(Long activatedAt, Long forTime, String timeUnit){
        long timeToAdd = switch (timeUnit) {
            case EventConstants.SEC -> forTime * 1000;
            case EventConstants.MIN -> forTime * 60 * 1000;
            case EventConstants.HOUR -> forTime * 60 * 60 * 1000;
            default -> throw new InvalidOperationException("Invalid time unit: " + timeUnit);
        };
        return activatedAt + timeToAdd;
    }

    public void handleNotificationMessage(Notification notification, String result){
        if (notification.getAlreadyTriggered()) {
            notification.setMultiplicityCounter(notification.getMultiplicityCounter() + 1);
            notification.setLastTimeTriggeredAt(Instant.now().toEpochMilli());
            System.out.println("already triggered:"+notification.getMultiplicityCounter()+" times");
        } else {
            notification.setAlreadyTriggered(true);
            notification.setFirstTimeTriggeredAt(Instant.now().toEpochMilli());
            if (notification.getLastTimeTriggeredAt() == null) {
                notification.setLastTimeTriggeredAt(Instant.now().toEpochMilli());
            }
            notification.setNotificationMessage(result.split(":")[1]);
            System.out.println("message:"+result.split(":")[1]);
        }
//        if(notification.getMutedUntil()!= null){
//            notification.setMutedUntil(null);
//        }
        notificationService.editNotification(notification);
    }

    public void handleNotificationForTime(Notification notification, String result){
        String forTimeSubType = result.split(":")[1];
        if(forTimeSubType.equals(EventConstants.FOR_TIME_SET)){
            if(notification.getForTimeCounterAlreadyTriggered()){
                Long timeUntil = calculateUntilTime(notification.getForTimeCounterActivatedAt(), Long.valueOf(result.split(":")[2]), result.split(":")[3]);
                if(Instant.now().toEpochMilli() >= timeUntil){
                    if(result.contains(EventConstants.NOTIFICATION_MESSAGE)){
                        handleNotificationMessage(notification, result.split(":")[4] + ":" + result.split(":")[5]);
                    }
                }
            }else{
                notification.setForTimeCounterAlreadyTriggered(true);
                notification.setForTimeCounterActivatedAt(Instant.now().toEpochMilli());
                notificationService.editNotification(notification);
            }
        } else if (forTimeSubType.equals(EventConstants.FOR_TIME_RESET)){
            notification.setForTimeCounterAlreadyTriggered(false);
            notification.setForTimeCounterActivatedAt(null);
            notificationService.editNotification(notification);
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