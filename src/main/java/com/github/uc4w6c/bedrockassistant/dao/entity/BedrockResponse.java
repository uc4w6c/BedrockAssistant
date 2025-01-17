package com.github.uc4w6c.bedrockassistant.dao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.OptionalInt;

public record BedrockResponse(
    String id,
    String type,
    String role,
    String model,
    List<BedrockContentResponse> content,
    @JsonProperty("stop_reason")
    String stopReason,
    @JsonProperty("stop_sequence")
    OptionalInt stopSequence,
    BedrockUsageResponse usage
) {}
