package ru.vprusakov.screenshoter;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CodeImageBuilder {

    CodeImageBuilder(AnActionEvent event){
        this.editor = event.getData(CommonDataKeys.EDITOR);
    }

    BufferedImage getSelectionScreenshot() {
        SelectionModel selectionModel = editor.getSelectionModel();
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();

        JComponent contentComponent = editor.getContentComponent();
        Rectangle rect = new Rectangle(new Point(0, 0));

        for (int i = start; i <= end; i++) {
            VisualPosition pos = editor.offsetToVisualPosition(i);
            Point point = editor.visualPositionToXY(pos);
            rect.add(point);
            rect.add(new Point(point.x, point.y + editor.getLineHeight()));
        }
        BufferedImage img = UIUtil.createImage(contentComponent, rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = img.getGraphics();
        graphics.translate(-rect.x, -rect.y);
        contentComponent.paint(graphics);

        return img;
    }

    private final Editor editor;
}
