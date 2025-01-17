package com.github.uc4w6c.bedrockassistant.dao.entity;

import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.domain.MessageRole;

public record BedrockMessageRequestEntity(String role, String content) {
  public static BedrockMessageRequestEntity of(Message message) {
    String role = switch (message.role()) {
      case USER -> "user";
      case ASSISTANT -> "assistant";
    };
    return new BedrockMessageRequestEntity(role, message.content());
  }
}
