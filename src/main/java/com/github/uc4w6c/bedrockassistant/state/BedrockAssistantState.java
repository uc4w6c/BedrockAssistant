package com.github.uc4w6c.bedrockassistant.state;

import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import com.github.uc4w6c.bedrockassistant.repository.ProfileRepository;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.regions.Region;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@State(
    name = "com.github.uc4w6c.bedrockassistant.presentation.state.BedrockAssistantState",
    storages = @Storage("BedrockAssistant.xml")
)
public class BedrockAssistantState implements PersistentStateComponent<BedrockAssistantState.State> {
  private State state = new State();

  private static final String DEFAULT_PROFILE = "default";
  private static final String DEFAULT_REGION = Region.US_EAST_1.id();

  public static BedrockAssistantState getInstance() {
    return ApplicationManager.getApplication()
        .getService(BedrockAssistantState.class);
  }

  @Override
  public void initializeComponent() {
    if (state.profile != null) {
      return;
    }

    ProfileRepository profileRepository = new ProfileRepository();
    Set<AwsProfile> profiles = profileRepository.getProfiles();
    if (profiles.isEmpty()) {
      state.profile = DEFAULT_PROFILE;
      state.region = DEFAULT_REGION;
      return;
    }

    Optional<AwsProfile> defaultProfile = profiles.stream()
        .filter(awsProfile -> Objects.equals(awsProfile.name(), "default"))
        .findFirst();

    Function<Optional<String>, String> getRegion = (optionalRegion) -> optionalRegion.orElse(DEFAULT_REGION);
    if (defaultProfile.isPresent()) {
      state.profile = DEFAULT_PROFILE;
      state.region = getRegion.apply(defaultProfile.get().region());
    } else {
      AwsProfile firstProfile = profiles.stream().findFirst().get();
      state.profile = firstProfile.name();
      state.region = getRegion.apply(firstProfile.region());
    }
    state.isAssistantEnabled = true;
  }

  @Override
  public State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }

  public static class State {
    public String profile;
    public String region;
    public boolean isAssistantEnabled;
  }
}
