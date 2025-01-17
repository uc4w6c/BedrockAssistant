package com.github.uc4w6c.bedrockassistant.dao;

import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.exceptions.TokenException;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

public class AssumeRoleRepository {
  /**
   *
   * @param mfaSerial
   * @param tokenCode
   * @return
   * @throws TokenException
   */
  public AwsCredentials getTokenWithMfa(String mfaSerial, String tokenCode) {
    GetSessionTokenRequest getSessionTokenRequest = GetSessionTokenRequest.builder()
        .tokenCode(tokenCode)
        .serialNumber(mfaSerial)
        .build();
    StsClient stsClient = StsClient.builder()
        .region(Region.AP_NORTHEAST_1)
        .build();

    try {
      GetSessionTokenResponse response = stsClient.getSessionToken(getSessionTokenRequest);

      return new AwsCredentials(
          response.credentials().accessKeyId(),
          response.credentials().secretAccessKey(),
          response.credentials().sessionToken(),
          response.credentials().expiration());
    } catch (Exception e) {
      throw new TokenException(e);
    }
  }

  public AwsCredentials getToken(String profileName) {
    ProfileCredentialsProvider provider = ProfileCredentialsProvider.builder()
        .profileFile(ProfileFileSupplier.defaultSupplier())
        .profileName(profileName)
        .build();

    GetSessionTokenRequest getSessionTokenRequest = GetSessionTokenRequest.builder()
        .build();

    StsClient stsClient = StsClient.builder()
        .credentialsProvider(provider)
        .build();

    try {
      GetSessionTokenResponse response = stsClient.getSessionToken(getSessionTokenRequest);

      return new AwsCredentials(
          response.credentials().accessKeyId(),
          response.credentials().secretAccessKey(),
          response.credentials().sessionToken(),
          response.credentials().expiration());
    } catch (Exception e) {
      throw new TokenException(e);
    }
  }
}
