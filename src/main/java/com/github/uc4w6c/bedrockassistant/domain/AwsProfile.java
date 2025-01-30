package com.github.uc4w6c.bedrockassistant.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * AWS Profile
 * @param name
 * @param region
 * @param roleArn
 * @param mfaSerial
 * @param roleSessionName
 */
public record AwsProfile(
    String name,
    Optional<String> region,
    Optional<String> roleArn,
    Optional<String> mfaSerial,
    Optional<String> roleSessionName
) {
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AwsProfile that)) return false;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }
}
