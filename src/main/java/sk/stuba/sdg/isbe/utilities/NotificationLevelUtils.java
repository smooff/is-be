package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

public final class NotificationLevelUtils {

    public static NotificationLevelEnum getNotificationLevelEnum(String notificationLevel) {
        try {
            return Enum.valueOf(NotificationLevelEnum.class, notificationLevel.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Notification resolution level: '" + notificationLevel + "' does not exist!");
        }
    }
}
