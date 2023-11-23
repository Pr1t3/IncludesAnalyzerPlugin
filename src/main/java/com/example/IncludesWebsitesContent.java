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
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static com.example.ReferenceChecker.isReferencePageExists;

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
        private final JEditorPane editorPane;
        public IncludeInfoToolWindowContent(Project project) {
            super(true, true);
            editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            editorPane.addHyperlinkListener(e -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(new URL(e.getURL().toString()).toURI());
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            JButton analyzeButton = new JButton("Analyze Includes");
            analyzeButton.addActionListener(e -> analyzeIncludes(project));
            setToolbar(analyzeButton);
            setContent(editorPane);
        }

        private void analyzeIncludes(Project project) {
            VirtualFile virtualFile = getCurrentFile(project);
            List<String> includes = IncludeAnalyzer.getIncludes(project, virtualFile);
            StringBuilder htmlContent = new StringBuilder("<html><body>");

            for (String include : includes) {
                boolean cppReferenceExists = isReferencePageExists(include);

                // Вставляем название инклюда и ссылку в HTML-контент

                if (cppReferenceExists) {
                    String referenceLink = "https://learn.microsoft.com/ru-ru/cpp/standard-library/" + include;
                    htmlContent.append("<p>").append(" - ").append("<a href=\"").append(referenceLink).append("\">").append(include).append("</a>").append("</p>");
                }
            }

            htmlContent.append("</body></html>");

            // Устанавливаем HTML-контент в JTextPane
            editorPane.setText(htmlContent.toString());
        }
    }
}
