package com.github.uc4w6c.bedrockassistant.state;

import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.repository.ProfileRepository;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@State(
    name = "com.github.uc4w6c.bedrockassistant.state.BedrockAssistantMessageCacheState",
    storages = @Storage(StoragePathMacros.CACHE_FILE)
)
public class BedrockAssistantMessageCacheState implements PersistentStateComponent<BedrockAssistantMessageCacheState.State> {
  private BedrockAssistantMessageCacheState.State state = new BedrockAssistantMessageCacheState.State();

  public static BedrockAssistantMessageCacheState getInstance() {
    return ApplicationManager.getApplication()
        .getService(BedrockAssistantMessageCacheState.class);
  }

  @Override
  public void initializeComponent() {
    BedrockAssistantMessageCacheState.State state = this.getState();
    if (state.messages == null) {
      state.messages = Collections.emptyList();
    }
  }

  @Override
  public BedrockAssistantMessageCacheState.State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull BedrockAssistantMessageCacheState.State state) {
    state = state;
  }

  public static class State {
    public List<Message> messages;
  }
}
