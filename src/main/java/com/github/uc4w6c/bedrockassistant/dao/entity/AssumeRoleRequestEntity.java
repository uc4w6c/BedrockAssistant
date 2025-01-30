package com.github.uc4w6c.bedrockassistant.dao.entity;

import java.util.Optional;

public record AssumeRoleRequestEntity(
    String roleArn,
    Optional<String> assumeRoleRegion,
    Optional<String> mfaSerial,
    Optional<String> tokenCode
) {
  public static class Builder {
    String roleArn = null;
    Optional<String> assumeRoleRegion = Optional.empty();
    Optional<String> mfaSerial = Optional.empty();
    Optional<String> tokenCode = Optional.empty();

    public Builder roleArn(String roleArn) {
      this.roleArn = roleArn;
      return this;
    }

    public Builder assumeRoleRegion(String assumeRoleRegion) {
      this.assumeRoleRegion = Optional.of(assumeRoleRegion);
      return this;
    }

    public Builder mfaSerial(String mfaSerial) {
      this.mfaSerial = Optional.of(mfaSerial);
      return this;
    }

    public Builder tokenCode(String tokenCode) {
      this.tokenCode = Optional.of(tokenCode);
      return this;
    }

    public AssumeRoleRequestEntity build() {
      if (this.roleArn == null) throw new IllegalArgumentException();
      if (this.mfaSerial.isPresent() != this.tokenCode.isPresent()) throw new IllegalArgumentException();

      return new AssumeRoleRequestEntity(
          this.roleArn,
          this.assumeRoleRegion,
          this.mfaSerial,
          this.tokenCode);
    }
  }
}
