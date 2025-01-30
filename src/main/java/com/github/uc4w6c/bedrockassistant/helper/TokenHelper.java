package com.github.uc4w6c.bedrockassistant.helper;

import com.github.uc4w6c.bedrockassistant.repository.AssumeRoleRepository;
import com.github.uc4w6c.bedrockassistant.domain.AwsCredentials;
import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import com.github.uc4w6c.bedrockassistant.exceptions.TokenException;
import com.github.uc4w6c.bedrockassistant.popup.MfaPopupComponent;
import com.github.uc4w6c.bedrockassistant.repository.ProfileRepository;
import com.github.uc4w6c.bedrockassistant.state.TokenManager;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

public class TokenHelper {
  private ProfileRepository profileRepository;
  private AssumeRoleRepository assumeRoleRepository;

  private final String TOKEN_ERROR = """
                    AWS token has not been obtained.
                    %s
                    """;

  public TokenHelper() {
    this.profileRepository = new ProfileRepository();
    this.assumeRoleRepository = new AssumeRoleRepository();
  }

  public Optional<AwsCredentials> getCredentials(Project project, String profileName) {
    Optional<AwsCredentials> optionalAwsCredentials = TokenManager.getCredentials();
    if (optionalAwsCredentials.isPresent()) return optionalAwsCredentials;

    Optional<AwsProfile> optionalAwsProfile = profileRepository.getProfile(profileName);

    if (optionalAwsProfile.isEmpty()) {
      NotificationGroupManager.getInstance()
          .getNotificationGroup("BedrockAssistantBalloon")
          .createNotification(String.format("The specified profile(%s) is not found.", profileName),
              NotificationType.ERROR)
          .notify(project);
      return Optional.empty();
    }
    AwsProfile awsProfile = optionalAwsProfile.get();
    if (awsProfile.roleArn().isEmpty()) {
      NotificationGroupManager.getInstance()
          .getNotificationGroup("BedrockAssistantBalloon")
          .createNotification(String.format("The role-arn is not specified for the specified profile(%s).", profileName),
              NotificationType.ERROR)
          .notify(project);
      return Optional.empty();
    }

    AssumeRoleRepository assumeRoleRepository = new AssumeRoleRepository();

    if (awsProfile.mfaSerial().isPresent()) {
      Consumer<String> okActionListener = (mfa) -> {
        if (StringUtils.isBlank(mfa)) {
          NotificationGroupManager.getInstance()
              .getNotificationGroup("BedrockAssistantBalloon")
              .createNotification("MFA is not entered.",
                  NotificationType.ERROR)
              .notify(project);
        }
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
          AwsCredentials awsCredentials = null;
          try {
            awsCredentials = assumeRoleRepository.getTokenWithMfa(awsProfile, mfa);
          } catch (TokenException e) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("BedrockAssistantBalloon")
                .createNotification(String.format(TOKEN_ERROR, e.getMessage()),
                    NotificationType.ERROR)
                .notify(project);
            return;
          }
          TokenManager.saveCredentials(awsCredentials);
          NotificationGroupManager.getInstance()
              .getNotificationGroup("BedrockAssistantBalloon")
              .createNotification("""
                      AWS token has been saved.
                      Please try again.
                      """,
                  NotificationType.INFORMATION)
              .notify(project);
        });
      };

      ApplicationManager.getApplication().invokeLater(() -> {
            new MfaPopupComponent.MfaPopupComponentBuilder()
                .mfaSerial(awsProfile.mfaSerial().get())
                .okActionListener(okActionListener)
                .build();
          });

      return Optional.empty();
    }

    AwsCredentials awsCredentials = null;
    try {
      awsCredentials = assumeRoleRepository.getToken(awsProfile);
    } catch (TokenException e) {
      NotificationGroupManager.getInstance()
          .getNotificationGroup("BedrockAssistantBalloon")
          .createNotification(String.format(TOKEN_ERROR, e.getMessage()),
              NotificationType.ERROR)
          .notify(project);
    }
    TokenManager.saveCredentials(awsCredentials);

    return Optional.of(awsCredentials);
  }
}
