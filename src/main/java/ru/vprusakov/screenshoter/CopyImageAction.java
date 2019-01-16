package ru.vprusakov.screenshoter;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

public class CopyImageAction extends com.intellij.openapi.actionSystem.AnAction {
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (event.getData(CommonDataKeys.EDITOR) == null) return;
        BufferedImage image = new CodeImageBuilder(event).getSelectionScreenshot();
        copyImage(image, event.getProject());
    }

    private void copyImage(BufferedImage image, Project project) {
        TransferableImage transferableImage = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(transferableImage, null);
        CodeImagePlugin.NOTIFICATION_GROUP
                .createNotification("Image was copied to clipboard", NotificationType.INFORMATION)
                .notify(project);
    }

    static class TransferableImage implements Transferable {
        private BufferedImage image;

        TransferableImage(BufferedImage image) {
            this.image = image;
        }

        @NotNull
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        presentation.setEnabled(editor != null && editor.getSelectionModel().hasSelection());
    }
}
