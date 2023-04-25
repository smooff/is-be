package sk.stuba.sdg.isbe.events;

import io.github.jamsesso.jsonlogic.JsonLogic;
import io.github.jamsesso.jsonlogic.JsonLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.repositories.StoredDataRepository;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.NotificationService;

import java.time.Instant;
import java.util.*;

@Component
public class NotificationProcessor {

    @Autowired
    NotificationService notificationService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    StoredDataRepository storedDataRepository;

    @Autowired
    JobService jobService;

    JsonLogic jsonLogic = new JsonLogic();

    @Async
    @EventListener
    public void handleDataSavedEvent(DataStoredEvent event) throws JsonLogicException {
        Calendar calendar = Calendar.getInstance();
        int actualDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int actualHour = calendar.get(Calendar.HOUR_OF_DAY);

        List<Notification> notifications = notificationRepository.getNotificationByDevicesContainingAndDeactivated(event.getDeviceId(), false);
        if (notifications != null) {
            for (StoredData storedData : event.getStoredData()) {
                Map<String, Double> dataForExpression = new HashMap<>();
                for (Notification notification : notifications) {
                    // FILTERING
                    // check, if notification is active only in some days
                    if (notification.getActiveAtDay().isEmpty() || notification.getActiveAtDay().contains(actualDayOfWeek)) {
                        // check, if notification is active only at some hours
                        if (notification.getActiveAtHour().isEmpty() || notification.getActiveAtHour().contains(actualHour)) {
                            // check, if notification associated with deviceId should be evaluating for this storedData
                            if (notification.getDeviceAndTag().get(event.getDeviceId()).contains(storedData.getTag())) {
                                // check, if notification is muted
                                if (notification.getMutedUntil() == null || (Instant.now().toEpochMilli() > notification.getMutedUntil())) {
                                    Map<String, List<String>> mapDeviceAndTag = notification.getDeviceAndTag();
                                    dataForExpression.put(storedData.getDeviceId() + storedData.getTag(), storedData.getValue());
                                    mapDeviceAndTag.forEach((k, v) -> {
                                        for (String tag : v) {
                                            if (k.equals(event.getDeviceId()) && tag.equals(storedData.getTag())) {
                                                // we can skip one DB call, because we already put actual storedData to dataForExpression
                                            } else {
                                                StoredData lastStoredData = storedDataRepository.findFirstStoredDataByDeviceIdAndTagOrderByMeasureAddDesc(k, tag);
                                                if (lastStoredData != null) {
                                                    dataForExpression.put(lastStoredData.getDeviceId() + lastStoredData.getTag(), lastStoredData.getValue());
                                                } else {
                                                    throw new InvalidOperationException("Notification with id: " + notification.getId() + " can not be evaluated, because of missing Stored Data for tag: " + tag);
                                                }
                                            }

                                        }
                                    });
                                    System.out.println("triggered");
                                    // desatinne cisla musia byt pisane s . -> 4.1
                                    String result = (String) jsonLogic.apply(notification.getRules(), dataForExpression);
                                    if (notification.getMutedUntil() != null) {
                                        notification.setMutedUntil(null);
                                    }
                                    if (result.contains(EventConstants.NO_ACTION)) {
                                        System.out.println("no action");
                                    } else if (result.contains(EventConstants.FOR_TIME)) {
                                        handleNotificationForTime(notification, result);
                                    } else if (result.contains(EventConstants.NOTIFICATION_MESSAGE)) {
                                        handleNotificationMessage(notification, result);
                                    } else if (result.contains(EventConstants.JOB)) {
                                        handleNotificationJob(notification, result);
                                    } else {
                                        throw new InvalidOperationException("Result: " + result + " not recognized.");
                                    }
                                    dataForExpression.clear();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Long calculateUntilTime(Long activatedAt, Long forTime, String timeUnit) {

        long timeToAdd = switch (timeUnit) {
            case EventConstants.SEC -> forTime * 1000;
            case EventConstants.MIN -> forTime * 60 * 1000;
            case EventConstants.HOUR -> forTime * 60 * 60 * 1000;
            default -> throw new InvalidOperationException("Invalid time unit: " + timeUnit);
        };

        return activatedAt + timeToAdd;
    }

    public void handleNotificationMessage(Notification notification, String result) {

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
            notification.getMessageMultiplicityCounter().put(message, notification.getMessageMultiplicityCounter().get(message) + 1);
        } else {
            notification.getMessageMultiplicityCounter().put(message, 1);
        }

        if (!notification.getAlreadyTriggered()) {
            notification.setAlreadyTriggered(true);
        }
        notificationService.editNotification(notification);
    }

    public void handleNotificationJob(Notification notification, String result) {
        String jobId = result.split(":")[1];

        Job job = jobService.getJobById(jobId);
        if (job.getCurrentStatus().equals(JobStatusEnum.JOB_DONE) || job.getCurrentStatus().equals(JobStatusEnum.JOB_ERR)) {
            jobService.resetJob(jobId);
            //trigger time (multiple values) for certain job
            if (notification.getJobAndTriggerTime().containsKey(jobId)) {
                notification.getJobAndTriggerTime().get(jobId).add(Instant.now().toEpochMilli());
            } else {
                List<Long> triggeredAt = new ArrayList<>();
                triggeredAt.add(Instant.now().toEpochMilli());
                notification.getJobAndTriggerTime().put(jobId, triggeredAt);
            }
            if (!notification.getAlreadyTriggered()) {
                notification.setAlreadyTriggered(true);
            }
            notificationService.editNotification(notification);
        }
    }

    public void handleNotificationForTime(Notification notification, String result) {

        String forTimeSubType = result.split(":")[1];

        if (forTimeSubType.equals(EventConstants.FOR_TIME_SET)) {

            String notificationReturnStatement = result.split(":")[4] + ":" + result.split(":")[5];

            //check if return statement was ever triggered
            if (notification.getForTimeCountingActivatedAt().containsKey(notificationReturnStatement)) {
                Long timeUntil = calculateUntilTime(notification.getForTimeCountingActivatedAt().get(notificationReturnStatement), Long.valueOf(result.split(":")[2]), result.split(":")[3]);

                if (Instant.now().toEpochMilli() >= timeUntil) {
                    if (result.contains(EventConstants.NOTIFICATION_MESSAGE)) {
                        handleNotificationMessage(notification, notificationReturnStatement);
                    } else if (result.contains(EventConstants.JOB)) {
                        handleNotificationJob(notification, notificationReturnStatement);
                    }
                }
            } else {
                notification.getForTimeCountingActivatedAt().put(notificationReturnStatement, Instant.now().toEpochMilli());
                notificationService.editNotification(notification);
            }
        } else if (forTimeSubType.equals(EventConstants.FOR_TIME_RESET)) {
            String notificationReturnStatement = result.split(":")[2] + ":" + result.split(":")[3];
            if(!notification.getForTimeCountingActivatedAt().isEmpty()){
                notification.getForTimeCountingActivatedAt().remove(notificationReturnStatement);
            }
            notificationService.editNotification(notification);
        }
    }
}