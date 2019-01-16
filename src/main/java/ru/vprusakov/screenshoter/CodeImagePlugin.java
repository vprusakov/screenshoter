package ru.vprusakov.screenshoter;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;

class CodeImagePlugin {
    static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup(
            "Code Screenshot Plugin",
            NotificationDisplayType.BALLOON,
            false,
            null
    );
}