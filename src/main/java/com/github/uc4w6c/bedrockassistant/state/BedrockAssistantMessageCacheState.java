package com.github.uc4w6c.bedrockassistant.state;

import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.domain.MessageRole;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BedrockAssistantMessageCacheState {
  public MessageRole role;
  public String content;

  public BedrockAssistantMessageCacheState() {
  }

  public BedrockAssistantMessageCacheState(MessageRole role, String content) {
    this.role = role;
    this.content = content;
  }

  public MessageRole getRole() {
    return role;
  }

  public void setRole(MessageRole role) {
    this.role = role;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Message toDomain() {
    return new Message(this.role, this.content);
  }

  public static BedrockAssistantMessageCacheState of(Message message) {
    return new BedrockAssistantMessageCacheState(message.role(), message.content());
  }
}
