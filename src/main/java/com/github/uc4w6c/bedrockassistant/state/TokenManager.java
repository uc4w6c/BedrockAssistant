package com.github.uc4w6c.bedrockassistant.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;

import java.time.Instant;
import java.util.Optional;

public class TokenManager {
  private static final String TOKEN_KEY = "com.github.uc4w6c.bedrockassistant.tokenKey";
  private static final String SUB_SYSTEM_NAME = "BedrockAssistant";

  public static void saveCredentials(AwsCredentials awsCredentials) {
    ObjectMapper objectMapper = createObjectMappner();

    try {
      PasswordSafe.getInstance().setPassword(new CredentialAttributes(
          CredentialAttributesKt.generateServiceName(SUB_SYSTEM_NAME, TOKEN_KEY)), objectMapper.writeValueAsString(awsCredentials));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static Optional<AwsCredentials> getCredentials() {
    String password = PasswordSafe.getInstance().getPassword(new CredentialAttributes(
        CredentialAttributesKt.generateServiceName(SUB_SYSTEM_NAME, TOKEN_KEY)));
    if (password == null) return Optional.empty();

    ObjectMapper objectMapper = createObjectMappner();

    AwsCredentials awsCredentials;
    try {
      awsCredentials = objectMapper.readValue(password, AwsCredentials.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if (!awsCredentials.isValid(Instant.now())) {
      deleteCredentials();
    }
    return Optional.of(awsCredentials);
  }

  public static void deleteCredentials() {
    PasswordSafe.getInstance().setPassword(new CredentialAttributes(
        CredentialAttributesKt.generateServiceName(SUB_SYSTEM_NAME, TOKEN_KEY)), null);
  }

  private static ObjectMapper createObjectMappner() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
}
