package com.github.uc4w6c.bedrockassistant.dao;

import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ProfileDao {
  public Map<String, Profile> getProfiles() {
    try {
      ProfileFile profileFile = ProfileFileSupplier.defaultSupplier().get();
      return profileFile.profiles();
    } catch (NullPointerException e) {
      // ignore NullPointerException
      return Collections.emptyMap();
    }
  }

  public Optional<Profile> getProfile(String profileName) {
    ProfileFile profileFile = ProfileFileSupplier.defaultSupplier().get();
    if (!profileFile.profiles().containsKey(profileName)) return Optional.empty();

    return Optional.of(profileFile.profiles().get(profileName));
  }
}
