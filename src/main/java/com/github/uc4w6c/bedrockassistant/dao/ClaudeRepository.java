package com.github.uc4w6c.bedrockassistant.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockMessageRequestEntity;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockRequestEntity;
import com.github.uc4w6c.bedrockassistant.dao.entity.BedrockResponse;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
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

public class ClaudeRepository {
  // TODO: make it possible to switch
  // private final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";
  private final String MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0";

  public String get(AwsCredentials awsCredentials, String bedrockRegion, List<Message> messages) {
    Region region = Region.of(bedrockRegion);

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

      AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.builder()
          .accessKeyId(awsCredentials.accessKeyId())
          .secretAccessKey(awsCredentials.secretAccessKey())
          .sessionToken(awsCredentials.sessionToken())
          .build();

      BedrockRuntimeClient client = BedrockRuntimeClient.builder()
          .region(region)
          .credentialsProvider(StaticCredentialsProvider.create(awsSessionCredentials))
          .build();

      InvokeModelRequest request = InvokeModelRequest.builder()
          .modelId(MODEL_ID)
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
