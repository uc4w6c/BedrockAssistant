package com.github.uc4w6c.bedrockassistant.action;

import com.github.uc4w6c.bedrockassistant.helper.TokenHelper;
import com.github.uc4w6c.bedrockassistant.dao.ClaudeRepository;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.domain.MessageRole;
import com.github.uc4w6c.bedrockassistant.exceptions.BedrockException;
import com.github.uc4w6c.bedrockassistant.service.BedrockAssistantToolWindowService;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantCacheState;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantState;
import com.github.uc4w6c.bedrockassistant.window.BedrockAssistantToolWindow;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnalyzeCodeAction extends AnAction {
  private final String MESSAGE_TO_ASK = """
      Explain the following code.

      ```
      %s
      ```
      """;

  @Override
  public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
    Project project = anActionEvent.getProject();
    if (project == null) throw new RuntimeException();;

    Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
    if (editor == null) {
      throw new RuntimeException();
    }
    String selectedText = editor.getSelectionModel().getSelectedText();
    if (StringUtils.isBlank(selectedText)) {
      return;
    }

    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
    ToolWindow toolWindow = toolWindowManager.getToolWindow("BedrockAssistantToolWindow");
    if (toolWindow == null) {
      throw new RuntimeException();
    }
    if (!toolWindow.isVisible()) {
      toolWindow.show();
    }

    BedrockAssistantToolWindow bedrockAssistantToolWindow = BedrockAssistantToolWindowService.getInstance(project)
        .getBedrockAssistantToolWindow();

    if (bedrockAssistantToolWindow == null) {
      throw new RuntimeException();
    }
    BedrockAssistantState.State state = BedrockAssistantState.getInstance().getState();

    TokenHelper tokenHelper = new TokenHelper();
    Optional<AwsCredentials> optionalAwsCredentials = tokenHelper.getCredentials(project, state.profile);

    if (optionalAwsCredentials.isEmpty()) {
      return;
    }
    String userMessage = String.format(MESSAGE_TO_ASK, selectedText);
    bedrockAssistantToolWindow.addUserMessage(userMessage);

    BedrockAssistantCacheState bedrockAssistantCacheState = BedrockAssistantCacheState.getInstance().getState();
    List<Message> cacheMessages = bedrockAssistantCacheState.getDomainMessages();
    List<Message> messages = new ArrayList<>(cacheMessages);
    messages.add(new Message(MessageRole.USER, userMessage));

    String response = null;
    try {
      ClaudeRepository claudeRepository = new ClaudeRepository();
      response = claudeRepository.get(optionalAwsCredentials.get(), state.region, messages);
    } catch (BedrockException e) {
      NotificationGroupManager.getInstance()
          .getNotificationGroup("BedrockAssistantBalloon")
          .createNotification(String.format("""
              Bedrock call failed.
              %s
              """, e.getMessage()),
              NotificationType.ERROR)
          .notify(project);
      return;
    }

    messages.add(new Message(MessageRole.ASSISTANT, response));
    bedrockAssistantCacheState.setDomainMessages(messages);

    bedrockAssistantToolWindow.addSystemMessage(response);
  }
}
