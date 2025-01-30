package com.github.uc4w6c.bedrockassistant.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockMessageRequestEntity;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockRequestEntity;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockResponse;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import com.github.uc4w6c.bedrockassistant.exceptions.BedrockException;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;

public class ClaudeDao {
  public String invoke(AwsSessionCredentials awsSessionCredentials, Region region, String modelId, List<Message> messages) {
    List<BedrockMessageRequestEntity> message =  messages.stream()
        .map(BedrockMessageRequestEntity::of)
        .toList();
    BedrockRequestEntity bedrockRequestEntity = new BedrockRequestEntity(
        "bedrock-2023-05-31",
        message,
        1024);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      String serialized = objectMapper.writeValueAsString(bedrockRequestEntity);

      BedrockRuntimeClient client = BedrockRuntimeClient.builder()
          .region(region)
          .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
          .build();

      InvokeModelRequest request = InvokeModelRequest.builder()
          .modelId(modelId)
          .body(SdkBytes.fromUtf8String(serialized))
          .build();

      InvokeModelResponse response = client.invokeModel(request);
      String body = response.body().asUtf8String();

      BedrockResponse bedrockResponse = objectMapper.readValue(body, BedrockResponse.class);
      return bedrockResponse.content().stream()
          .reduce((first, second) -> second)
          .get()
          .text();
    } catch (Exception e) {
      throw new BedrockException(e);
    }
  }
}
