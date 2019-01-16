package ru.vprusakov.screenshoter;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class CodeImageBuilder {

    CodeImageBuilder(AnActionEvent event){
        this.project = event.getProject();
        this.editor = event.getData(CommonDataKeys.EDITOR);
        this.document = Objects.requireNonNull(event.getData(PlatformDataKeys.EDITOR)).getDocument();
    }

    BufferedImage getSelectionScreenshot() {
        SelectionModel selectionModel = editor.getSelectionModel();
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        selectionModel.setSelection(0, 0);

        CaretModel caretModel = editor.getCaretModel();
        int lastLine = document.getLineCount();
        int lastLineOffset = document.getLineEndOffset(lastLine - 1);
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.insertString(lastLineOffset, "\n")
        );
        caretModel.moveToOffset(lastLineOffset + 1);

        JComponent contentComponent = editor.getContentComponent();
        Rectangle rect = new Rectangle(getPoint(editor, start));

        for (int i = start; i <= end; i++) {
            Point point = getPoint(editor, i);
            rect.add(point);
            rect.add(new Point(point.x, point.y + editor.getLineHeight()));
        }
        BufferedImage img = UIUtil.createImage(contentComponent, rect.width, rect.height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = img.getGraphics();
        graphics.translate(-rect.x, -rect.y);
        contentComponent.paint(graphics);

        selectionModel.setSelection(start, end);
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(lastLineOffset, lastLineOffset + 1, "")
        );
        caretModel.moveToOffset(end);

        return img;
    }

    private Point getPoint(com.intellij.openapi.editor.Editor editor, int offset) {
        VisualPosition pos = editor.offsetToVisualPosition(offset);
        return editor.visualPositionToXY(pos);
    }

    private final Project project;
    private final Editor editor;
    private final Document document;
}
