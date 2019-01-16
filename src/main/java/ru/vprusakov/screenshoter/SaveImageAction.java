package ru.vprusakov.screenshoter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class SaveImageAction extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent event) {
        BufferedImage image = new CodeImageBuilder(event).getSelectionScreenshot();
    }

}
