package com.github.uc4w6c.bedrockassistant.window;

import com.github.uc4w6c.bedrockassistant.repository.BedrockRepository;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.domain.MessageRole;
import com.github.uc4w6c.bedrockassistant.exceptions.BedrockException;
import com.github.uc4w6c.bedrockassistant.helper.TokenHelper;
import com.github.uc4w6c.bedrockassistant.service.BedrockAssistantToolWindowService;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantCacheState;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantState;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BedrockAssistantToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

    Function<String, String> sendActionListener = (value) -> {
      BedrockAssistantState.State state = BedrockAssistantState.getInstance().getState();

      TokenHelper tokenHelper = new TokenHelper();
      Optional<AwsCredentials> optionalAwsCredentials = tokenHelper.getCredentials(project, state.profile);

      BedrockAssistantCacheState bedrockAssistantCacheState = BedrockAssistantCacheState.getInstance().getState();
      List<Message> cacheMessages = bedrockAssistantCacheState.getDomainMessages();
      List<Message> messages = new ArrayList<>(cacheMessages);
      messages.add(new Message(MessageRole.USER, value));

      String response = null;
      try {
        BedrockRepository bedrockRepository = new BedrockRepository();
        response = bedrockRepository.get(optionalAwsCredentials.get(), state.region, messages);
      } catch (BedrockException e) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("BedrockAssistantBalloon")
            .createNotification(String.format("""
                  Bedrock call failed.
                  %s
                  """, e.getMessage()),
                NotificationType.ERROR)
            .notify(project);

        return null;
      }
      messages.add(new Message(MessageRole.ASSISTANT, response));
      bedrockAssistantCacheState.setDomainMessages(messages);

      return response;
    };
    BedrockAssistantToolWindow bedrockAssistantToolWindow = new BedrockAssistantToolWindow(sendActionListener);
    Content content = ContentFactory.getInstance().createContent(bedrockAssistantToolWindow.getContent(), null, false);
    toolWindow.getContentManager().addContent(content);

    BedrockAssistantToolWindowService.getInstance(project).setBedrockAssistantToolWindow(bedrockAssistantToolWindow);
  }
}
