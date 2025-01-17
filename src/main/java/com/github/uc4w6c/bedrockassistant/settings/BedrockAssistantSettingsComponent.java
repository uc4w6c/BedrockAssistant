package com.github.uc4w6c.bedrockassistant.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;
import java.util.Set;

public class BedrockAssistantSettingsComponent {
  private final JPanel mainPanel;
  private final JComboBox<String> profileComboBox;
  private final JComboBox<String> regionComboBox;

  public BedrockAssistantSettingsComponent(Set<String> profiles, Set<String> regions) {
    profileComboBox = new JComboBox<>(profiles.toArray(String[]::new));
    regionComboBox = new JComboBox<>(regions.toArray(String[]::new));

    mainPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(new JBLabel("Profile:"), profileComboBox, 1, false)
        .addLabeledComponent(new JBLabel("Bedrock region:"), regionComboBox, 1, false)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return profileComboBox;
  }

  public String getProfile() {
    return (String) profileComboBox.getSelectedItem();
  }

  public void setSelectedItemToProfileComboBox(String profile) {
    profileComboBox.setSelectedItem(profile);
  }

  public String getRegion() {
    return (String) regionComboBox.getSelectedItem();
  }

  public void setSelectedItemToRegionComboBox(String region) {
    regionComboBox.setSelectedItem(region);
  }
}
