package ru.vprusakov.screenshoter;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@State(
        name = "SaveImageOptionsProvider",
        storages = {
                @Storage(StoragePathMacros.WORKSPACE_FILE)
        }
)
public class SaveImageOptionsProvider implements PersistentStateComponent<SaveImageOptionsProvider.State> {
    private State myState = new State();

    static SaveImageOptionsProvider getInstance(Project project) {
        return ServiceManager.getService(project, SaveImageOptionsProvider.class);
    }

    @NotNull
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState.myDirectoryToSave = state.myDirectoryToSave;
    }


    public static class State {
        public String myDirectoryToSave = null;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equals(myDirectoryToSave, state.myDirectoryToSave);
        }

        @Override
        public int hashCode() {
            return Objects.hash(myDirectoryToSave);
        }
    }
}