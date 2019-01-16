package ru.vprusakov.screenshoter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveImageAction extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent event) {
        BufferedImage image = new CodeImageBuilder(event).getSelectionScreenshot();
        saveImage(image, event.getProject());
    }

    private void saveImage(BufferedImage codeImage, Project project) {
        String fileDirPath = System.getProperty("user.home");
        String fileSeparator = System.getProperty("file.separator");
        String fileTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "code-image_" + fileTimeStamp;
        String fileFormat = "png";
        String filePath = fileDirPath + fileSeparator + fileName + "." + fileFormat;

        File file = new File(filePath);
        try {
            ImageIO.write(codeImage, fileFormat, file);
        } catch (IOException e) {
            System.out.println("An error occurred during writing.");
        }
    }

}
