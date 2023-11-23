package com.example;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class IncludesWebsitesContent implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        IncludeInfoToolWindowContent toolWindowContent = new IncludeInfoToolWindowContent(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static class IncludeInfoToolWindowContent extends SimpleToolWindowPanel {
        private VirtualFile getCurrentFile(Project project) {
            FileEditorManager editorManager = FileEditorManager.getInstance(project);
            VirtualFile[] selectedFiles = editorManager.getSelectedFiles();

            if (selectedFiles.length > 0) {
                return selectedFiles[0];
            }

            return null;
        }
        private final JTextArea textArea;
        public IncludeInfoToolWindowContent(Project project) {
            super(true, true);
            textArea = new JTextArea();
            textArea.setEditable(false);
            JButton analyzeButton = new JButton("Analyze Includes");
            analyzeButton.addActionListener(e -> analyzeIncludes(project));
            setToolbar(analyzeButton);
            setContent(textArea);
        }

        private void analyzeIncludes(Project project) {
            VirtualFile virtualFile = getCurrentFile(project);
            List<String> includes = IncludeAnalyzer.getIncludes(project, virtualFile);
            textArea.setText("");
            for (String include : includes) {
                textArea.append(include + "\n");
            }
        }
    }
}
