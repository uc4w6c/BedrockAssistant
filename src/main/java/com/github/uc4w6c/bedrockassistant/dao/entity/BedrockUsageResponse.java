package com.github.uc4w6c.bedrockassistant.dao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BedrockUsageResponse(
    @JsonProperty("input_tokens")
    int inputTokens,
    @JsonProperty("output_tokens")
    int outputTokens
) {}
