package com.github.uc4w6c.bedrockassistant.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AwsCredentialsTest {
  @Nested
  class isValid {
    @Test
    void returnsTrueIfCurrentTimeIsBeforeExpiration() {
      AwsCredentials awsCredentials = new AwsCredentials(
          "accessKeyId",
          "secretAccessKey",
          "sessionToken",
          Instant.parse("2025-01-10T06:30:10.00Z")
      );
      Instant now = Instant.parse("2025-01-10T06:30:04.00Z");

      assertTrue(awsCredentials.isValid(now));
    }

    @Test
    void returnsFalseIfCurrentTimeIsSameAsExpiration() {
      AwsCredentials awsCredentials = new AwsCredentials(
          "accessKeyId",
          "secretAccessKey",
          "sessionToken",
          Instant.parse("2025-01-10T06:30:10.00Z")
      );
      Instant now = Instant.parse("2025-01-10T06:30:05.00Z");

      assertFalse(awsCredentials.isValid(now));
    }
  }
}
