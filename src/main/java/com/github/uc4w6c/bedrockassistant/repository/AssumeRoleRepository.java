package com.github.uc4w6c.bedrockassistant.repository;

import com.github.uc4w6c.bedrockassistant.dao.AssumeRoleDao;
import com.github.uc4w6c.bedrockassistant.dao.entity.AssumeRoleRequestEntity;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import com.github.uc4w6c.bedrockassistant.exceptions.TokenException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

public class AssumeRoleRepository {
  private final AssumeRoleDao assumeRoleDao;

  public AssumeRoleRepository() {
    this.assumeRoleDao = new AssumeRoleDao();
  }

  /**
   *
   * @param profile
   * @param tokenCode
   * @return
   * @throws TokenException
   */
  public AwsCredentials getTokenWithMfa(AwsProfile profile, String tokenCode) {
    AssumeRoleRequestEntity.Builder builder = new AssumeRoleRequestEntity.Builder();
    builder.roleArn(profile.roleArn().get());
    if (profile.region().isPresent()) {
      builder.assumeRoleRegion(profile.region().get());
    }
    builder.mfaSerial(profile.mfaSerial().get());
    builder.tokenCode(tokenCode);

    AssumeRoleResponse response = assumeRoleDao.getToken(builder.build());
    return new AwsCredentials(
        response.credentials().accessKeyId(),
        response.credentials().secretAccessKey(),
        response.credentials().sessionToken(),
        response.credentials().expiration());
  }

  public AwsCredentials getToken(AwsProfile profile) {
    AssumeRoleRequestEntity request = new AssumeRoleRequestEntity.Builder()
        .roleArn(profile.roleArn().get())
        .build();

    AssumeRoleResponse response = assumeRoleDao.getToken(request);
    return new AwsCredentials(
        response.credentials().accessKeyId(),
        response.credentials().secretAccessKey(),
        response.credentials().sessionToken(),
        response.credentials().expiration());
  }
}
