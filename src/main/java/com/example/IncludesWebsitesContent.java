package com.example;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            JBScrollPane scrollPane = new JBScrollPane(editorPane);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            JButton analyzeButton = new JButton("Analyze Includes");
            analyzeButton.addActionListener(e -> analyzeIncludes(project));
            setToolbar(analyzeButton);
            setContent(scrollPane);
        }
        private String getAdditionalInfo(String site) {
            String additionalInfo = "";
            try {
                Document document = Jsoup.connect(site).get();

                Element contentElement = document.selectFirst(".content p");
                if (contentElement != null) {
                    additionalInfo = contentElement.text().trim();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return additionalInfo;
        }

        private void analyzeIncludes(Project project) {
            VirtualFile virtualFile = getCurrentFile(project);
            List<String> includes = IncludeAnalyzer.getIncludes(virtualFile);
            StringBuilder htmlContent = new StringBuilder("<html><body>");
            Set<String> set = new HashSet<>(includes);
            includes.clear();
            includes.addAll(set);
            for (String include : includes) {
                String referenceLink = "https://learn.microsoft.com/en-us/cpp/standard-library/" + include;
                boolean referenceExists = isReferencePageExists(referenceLink);

                if (referenceExists) {
                    String information = getAdditionalInfo(referenceLink);
                    htmlContent.append("<p>").append(" - ").append("<a href=\"").append(referenceLink).append("\">").append(include).append("</a>").append(": ").append(information).append("</p>");
                }
            }

            htmlContent.append("</body></html>");

            editorPane.setText(htmlContent.toString());
        }
    }
}
