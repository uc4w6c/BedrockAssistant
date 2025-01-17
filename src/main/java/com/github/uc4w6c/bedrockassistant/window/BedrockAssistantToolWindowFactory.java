package com.github.uc4w6c.bedrockassistant.window;

import com.github.uc4w6c.bedrockassistant.service.BedrockAssistantToolWindowService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class BedrockAssistantToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    Function<String, String> sendActionListener = (value) -> {
      return value;
    };
    BedrockAssistantToolWindow bedrockAssistantToolWindow = new BedrockAssistantToolWindow(sendActionListener);
    Content content = ContentFactory.getInstance().createContent(bedrockAssistantToolWindow.getContent(), null, false);
    toolWindow.getContentManager().addContent(content);

    BedrockAssistantToolWindowService.getInstance(project).setBedrockAssistantToolWindow(bedrockAssistantToolWindow);
  }
}
