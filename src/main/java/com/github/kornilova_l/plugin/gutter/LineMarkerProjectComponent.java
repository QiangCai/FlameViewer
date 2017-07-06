package com.github.kornilova_l.plugin.gutter;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LineMarkerProjectComponent extends AbstractProjectComponent {
    protected LineMarkerProjectComponent(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        LineMarkersHolder lineMarkersHolder = myProject.getComponent(LineMarkersHolder.class);
        MessageBusConnection connection = myProject.getMessageBus().connect();
        PsiManagerImpl.getInstance(myProject)
                .addPsiTreeChangeListener(new UpdatingMarkersPsiTreeChangeListener(lineMarkersHolder, myProject));
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                if (Objects.equals(file.getFileType().getDefaultExtension(), "java")) {
                    PsiFile[] psiFiles = FilenameIndex.getFilesByName(
                            myProject,
                            file.getName(),
                            GlobalSearchScope.fileScope(myProject, file));
                    if (psiFiles.length != 1) {
                        return;
                    }
                    PsiFile psiFile = psiFiles[0];
                    Document document = psiFile.getViewProvider().getDocument();
                    if (document == null) {
                        return;
                    }
                    lineMarkersHolder.updateMethodMarker(psiFile, document);
                }
            }
        });
    }
}
