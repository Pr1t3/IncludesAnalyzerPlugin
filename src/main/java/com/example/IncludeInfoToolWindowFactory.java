package com.example;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

public class IncludeInfoToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        IncludesWebsitesContent.IncludeInfoToolWindowContent content = new IncludesWebsitesContent.IncludeInfoToolWindowContent(project);
        toolWindow.getComponent().getParent().add(content);
    }
}
