package ru.vprusakov.screenshoter;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.SwingHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SaveImageConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private SaveImageOptionsPanel myPanel;
    private final SaveImageOptionsProvider myOptionsProvider;
    private final Project myProject;


    public SaveImageConfigurable(SaveImageOptionsProvider optionsProvider, @NotNull Project project) {
        this.myProject = project;
        myOptionsProvider = optionsProvider;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Save code as image";
    }

    @NotNull
    @Override
    public String getId() {
        return "screenshoter";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myPanel = new SaveImageOptionsPanel();
        return myPanel.myWholePanel;
    }

    @Override
    public boolean isModified() {
        return !myOptionsProvider.getState().equals(myPanel.toState());
    }

    @Override
    public void apply() {
        myOptionsProvider.loadState(myPanel.toState());
    }

    @Override
    public void reset()  {
        myPanel.fromState(myOptionsProvider.getState());
    }

    @Override
    public void disposeUIResources() {
        myPanel = null;
    }

    public class SaveImageOptionsPanel {

        private JPanel myWholePanel;
        private JPanel mySaveDirectoryPanel;
        private TextFieldWithHistoryWithBrowseButton mySaveDirectory;

        SaveImageOptionsProvider.State toState() {
            SaveImageOptionsProvider.State state = new SaveImageOptionsProvider.State();
            state.myDirectoryToSave = StringUtil.nullize(mySaveDirectory.getText());
            return state;
        }

        void fromState(SaveImageOptionsProvider.State state) {
            mySaveDirectory.setText(StringUtil.notNullize(state.myDirectoryToSave));
        }

        private void createUIComponents() {
            FileChooserDescriptor singleFolderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            TextFieldWithHistoryWithBrowseButton field = SwingHelper.createTextFieldWithHistoryWithBrowseButton(myProject,
                    "Save to directory",
                    singleFolderDescriptor,
                    ContainerUtil::emptyList);
            mySaveDirectoryPanel = field;
            mySaveDirectory = field;
        }
    }
}

