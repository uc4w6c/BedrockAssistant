package com.github.uc4w6c.bedrockassistant.dao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BedrockRequestEntity(
    @JsonProperty("anthropic_version")
    String anthropicVersion,
    List<BedrockMessageRequestEntity> messages,
    @JsonProperty("max_tokens")
    Integer maxTokens) {
}
