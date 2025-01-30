package com.github.uc4w6c.bedrockassistant.dao;

import com.github.uc4w6c.bedrockassistant.dao.entity.AssumeRoleRequestEntity;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.exceptions.TokenException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;

import java.util.UUID;

public class AssumeRoleDao {
  public AssumeRoleResponse getToken(AssumeRoleRequestEntity requestEntity) {
    AssumeRoleRequest.Builder requestBuilder = AssumeRoleRequest
        .builder()
        .roleArn(requestEntity.roleArn())
        .roleSessionName(UUID.randomUUID().toString());

    if (requestEntity.mfaSerial().isPresent()) {
      requestBuilder.serialNumber(requestEntity.mfaSerial().get());
      requestBuilder.tokenCode(requestEntity.tokenCode().get());
    }
    AssumeRoleRequest request = requestBuilder.build();

    StsClientBuilder stsClientBuilder = StsClient.builder();
    if (requestEntity.assumeRoleRegion().isPresent()) {
      Region region = Region.of(requestEntity.assumeRoleRegion().get());
      stsClientBuilder.region(region);
    }

    try(StsClient stsClient = stsClientBuilder.build()) {
      return stsClient.assumeRole(request);
    } catch (Exception e) {
      throw new TokenException(e);
    }
  }
}
