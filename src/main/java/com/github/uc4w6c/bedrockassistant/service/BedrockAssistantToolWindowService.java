package com.github.uc4w6c.bedrockassistant.service;

import com.github.uc4w6c.bedrockassistant.window.BedrockAssistantToolWindow;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
public final class BedrockAssistantToolWindowService {
  private BedrockAssistantToolWindow bedrockAssistantToolWindow;

  public static BedrockAssistantToolWindowService getInstance(@NotNull Project project) {
    return project.getService(BedrockAssistantToolWindowService.class);
  }

  public void setBedrockAssistantToolWindow(BedrockAssistantToolWindow bedrockAssistantToolWindow) {
    this.bedrockAssistantToolWindow = bedrockAssistantToolWindow;
  }

  public BedrockAssistantToolWindow getBedrockAssistantToolWindow() {
    return bedrockAssistantToolWindow;
  }
}
