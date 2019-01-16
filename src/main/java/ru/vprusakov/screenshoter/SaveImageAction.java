package ru.vprusakov.screenshoter;

import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageAction extends AnAction {

    public void actionPerformed(@NotNull AnActionEvent event) {
        if (event.getData(CommonDataKeys.EDITOR) == null) return;
        BufferedImage image = new CodeImageBuilder(event).getSelectionScreenshot();
        saveImage(image, event.getProject());
    }

    private void saveImage(BufferedImage codeImage, Project project) {

        String fileDirPath;
        String fileSeparator;

        SaveImageOptionsProvider.State options = SaveImageOptionsProvider.getInstance(project).getState();
        fileDirPath = options.myDirectoryToSave;

        try {
            if (StringUtil.isEmpty(fileDirPath)) {
                fileDirPath = getUserSystemProperty("user.home");
            }
            fileSeparator = getUserSystemProperty("file.separator");
        } catch (IllegalArgumentException | SecurityException e) {
            exceptionNotify(e.getMessage(), project);
            return;
        }

        String fileTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "code-image_" + fileTimeStamp;
        String fileFormat = "png";
        String filePath = fileDirPath + fileSeparator + fileName + "." + fileFormat;

        File file = new File(filePath);
        try {
            ImageIO.write(codeImage, fileFormat, file);
        } catch (IOException e) {
            exceptionNotify("An error occurred during writing.", project);
            return;
        }

        NotificationListener listener = (notification, hyperlinkEvent) -> {
            try {
                Desktop.getDesktop().open(file);
            } catch (IllegalArgumentException  e) {
                exceptionNotify("File doesn't exist", project);
            } catch (IOException e) {
                exceptionNotify("Cannot open image", project);
            }
        };

        String openLink = Desktop.isDesktopSupported() ? "<a href=''>Open</a>" : "";

        CodeImagePlugin.NOTIFICATION_GROUP
                .createNotification(
                        "Image was saved",
                        filePath + "<br>" + openLink,
                        NotificationType.INFORMATION,
                        listener)
                .notify(project);
    }

    private String getUserSystemProperty(String property) {
        try {
            String value = System.getProperty(property);
            if (value == null) {
                throw new IllegalArgumentException("System doesn't contain property: " + property);
            }
            return value;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Empty property was passed", e);
        } catch (SecurityException e) {
            throw new SecurityException("Security manager doesn't allow access to the system property: " + property, e);
        }
    }

    private void exceptionNotify(String error, Project project) {
        CodeImagePlugin.NOTIFICATION_GROUP
                .createNotification("An error occurred:", error, NotificationType.ERROR, null)
                .notify(project);
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        presentation.setEnabled(editor != null && editor.getSelectionModel().hasSelection());
    }
}