package com.github.uc4w6c.bedrockassistant.repository;

import com.github.uc4w6c.bedrockassistant.dao.ClaudeDao;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.Message;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import java.util.List;

public class BedrockRepository {
  private final ClaudeDao claudeDao;

  public BedrockRepository() {
    this.claudeDao = new ClaudeDao();
  }

  // TODO: make it possible to switch
  private final String MODEL_ID = "anthropic.claude-3-5-sonnet-20240620-v1:0";
  // private final String MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0";

  public String get(AwsCredentials awsCredentials, String bedrockRegion, List<Message> messages) {
    AwsSessionCredentials awsSessionCredentials = AwsSessionCredentials.builder()
        .accessKeyId(awsCredentials.accessKeyId())
        .secretAccessKey(awsCredentials.secretAccessKey())
        .sessionToken(awsCredentials.sessionToken())
        .build();

    Region region = Region.of(bedrockRegion);

    return claudeDao.invoke(awsSessionCredentials, region, MODEL_ID, messages);
  }
}
