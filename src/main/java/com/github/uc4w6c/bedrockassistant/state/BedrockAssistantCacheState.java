package com.github.uc4w6c.bedrockassistant.state;

import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@State(
    name = "com.github.uc4w6c.bedrockassistant.state.BedrockAssistantCacheState",
    storages = @Storage(StoragePathMacros.CACHE_FILE)
)
public class BedrockAssistantCacheState implements PersistentStateComponent<BedrockAssistantCacheState> {
  public List<BedrockAssistantMessageCacheState> messages = new ArrayList<>();

  public static BedrockAssistantCacheState getInstance() {
    return ApplicationManager.getApplication()
        .getService(BedrockAssistantCacheState.class);
  }

  @Override
  public BedrockAssistantCacheState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull BedrockAssistantCacheState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public List<Message> getDomainMessages() {
    if (messages == null) return Collections.emptyList();
    return messages.stream().map(BedrockAssistantMessageCacheState::toDomain).toList();
  }

  public void setDomainMessages(List<Message> messages) {
    this.messages = messages.stream().map(BedrockAssistantMessageCacheState::of).toList();
  }
}
