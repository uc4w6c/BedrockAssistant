package com.github.uc4w6c.bedrockassistant.domain;

import java.time.Instant;

public record AwsCredentials(
    String accessKeyId,
    String secretAccessKey,
    String sessionToken,
    Instant expiration
    ) {
  private static final long EXPIRATION_BUFFER_SECONDS = 5;

  public boolean isValid(Instant now) {
    return now.isBefore(this.expiration.minusSeconds(EXPIRATION_BUFFER_SECONDS));
  }
}
