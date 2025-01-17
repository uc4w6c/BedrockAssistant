package com.github.uc4w6c.bedrockassistant.settings;

import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import com.github.uc4w6c.bedrockassistant.repository.ProfileRepository;
import com.github.uc4w6c.bedrockassistant.state.BedrockAssistantState;
import com.github.uc4w6c.bedrockassistant.state.TokenManager;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import software.amazon.awssdk.regions.Region;

import javax.swing.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class BedrockAssistantSettingsConfigurable implements Configurable {

  private BedrockAssistantSettingsComponent bedrockAssistantSettingsComponent;

  public BedrockAssistantSettingsConfigurable() {}

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "BedrockAssistant";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return bedrockAssistantSettingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    BedrockAssistantState.State state =
        Objects.requireNonNull(BedrockAssistantState.getInstance().getState());

    ProfileRepository profileRepository = new ProfileRepository();

    bedrockAssistantSettingsComponent = new BedrockAssistantSettingsComponent(
        profileRepository.getProfiles().stream().map(AwsProfile::name).collect(Collectors.toSet()),
        Region.regions().stream().map(Region::id).collect(Collectors.toSet()));

    bedrockAssistantSettingsComponent.setSelectedItemToProfileComboBox(state.profile);
    bedrockAssistantSettingsComponent.setSelectedItemToRegionComboBox(state.region);
    return bedrockAssistantSettingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    BedrockAssistantState.State state =
        Objects.requireNonNull(BedrockAssistantState.getInstance().getState());
    return !Objects.equals(bedrockAssistantSettingsComponent.getProfile(), state.profile) ||
        !Objects.equals(bedrockAssistantSettingsComponent.getRegion(), state.region);
  }

  @Override
  public void apply() {
    BedrockAssistantState.State state =
        Objects.requireNonNull(BedrockAssistantState.getInstance().getState());

    if (!Objects.equals(state.profile, bedrockAssistantSettingsComponent.getProfile())) {
      TokenManager.deleteCredentials();
    }

    state.profile = bedrockAssistantSettingsComponent.getProfile();
    state.region = bedrockAssistantSettingsComponent.getRegion();
  }

  @Override
  public void reset() {
    BedrockAssistantState.State state =
        Objects.requireNonNull(BedrockAssistantState.getInstance().getState());
    bedrockAssistantSettingsComponent.setSelectedItemToProfileComboBox(state.profile);
    bedrockAssistantSettingsComponent.setSelectedItemToRegionComboBox(state.region);
  }

  @Override
  public void disposeUIResources() {
    bedrockAssistantSettingsComponent = null;
  }
}
