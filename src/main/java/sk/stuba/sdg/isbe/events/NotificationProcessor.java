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
                                    }else {
                                        throw new InvalidOperationException("Notification with id: "+notification.getId()+" can not be evaluated, because of missing Stored Data for tag: "+tag);
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
                            } else if (result.contains(EventConstants.JOB)) {
                                handleNotificationJob(notification, result);
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

        String message = result.split(":")[1];

        //trigger time (multiple values) for certain message
        if (notification.getMessageAndTriggerTime().containsKey(message)) {
            notification.getMessageAndTriggerTime().get(message).add(Instant.now().toEpochMilli());
        } else {
            List<Long> triggeredAt = new ArrayList<>();
            triggeredAt.add(Instant.now().toEpochMilli());
            notification.getMessageAndTriggerTime().put(message, triggeredAt);
        }
        //trigger count for certain message
        if (notification.getMessageMultiplicityCounter().containsKey(message)) {
            notification.getMessageMultiplicityCounter().put(message, notification.getMessageMultiplicityCounter().get(message)+1);
        }else{
            notification.getMessageMultiplicityCounter().put(message, 1);
        }

        if(!notification.getAlreadyTriggered()){
            notification.setAlreadyTriggered(true);
        }
        notificationService.editNotification(notification);
    }

    public void handleNotificationJob(Notification notification, String result){
        String jobId = result.split(":")[1];
        // dotiahnut si job s jobId(ktory by sa mal spustit) -> pozriet sa na tento job a na jeho retCode/code -> podla toho sa spusti job


//        if (notification.getAlreadyTriggered()) {
//            notification.setMultiplicityCounter(notification.getMultiplicityCounter() + 1);
//            notification.setLastTimeTriggeredAt(Instant.now().toEpochMilli());
//            System.out.println("already triggered:"+notification.getMultiplicityCounter()+" times");
//        } else {
//            notification.setAlreadyTriggered(true);
//            notification.setFirstTimeTriggeredAt(Instant.now().toEpochMilli());
//            if (notification.getLastTimeTriggeredAt() == null) {
//                notification.setLastTimeTriggeredAt(Instant.now().toEpochMilli());
//            }
//            notification.setNotificationMessage(result.split(":")[1]);
//            System.out.println("message:"+result.split(":")[1]);
//        }
//        if(notification.getMutedUntil()!= null){
//            notification.setMutedUntil(null);
//        }
        if(!notification.getAlreadyTriggered()){
            notification.setAlreadyTriggered(true);
        }
        notificationService.editNotification(notification);
    }

    public void handleNotificationForTime(Notification notification, String result){

        String forTimeSubType = result.split(":")[1];

        if(forTimeSubType.equals(EventConstants.FOR_TIME_SET)){

            String notificationReturnStatement = result.split(":")[4] + ":" + result.split(":")[5];

            //check if return statement was ever triggered
            if(notification.getForTimeCountingActivatedAt().containsKey(notificationReturnStatement)){
                Long timeUntil = calculateUntilTime(notification.getForTimeCountingActivatedAt().get(notificationReturnStatement), Long.valueOf(result.split(":")[2]), result.split(":")[3]);

                if(Instant.now().toEpochMilli() >= timeUntil){
                    if(result.contains(EventConstants.NOTIFICATION_MESSAGE)){
                        handleNotificationMessage(notification, notificationReturnStatement);
                    }
                }
            } else{
                notification.getForTimeCountingActivatedAt().put(notificationReturnStatement, Instant.now().toEpochMilli());
                notificationService.editNotification(notification);
            }
        } else if (forTimeSubType.equals(EventConstants.FOR_TIME_RESET)){
            String notificationReturnStatement = result.split(":")[2] + ":" + result.split(":")[3];
            notification.getForTimeCountingActivatedAt().remove(notificationReturnStatement);
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