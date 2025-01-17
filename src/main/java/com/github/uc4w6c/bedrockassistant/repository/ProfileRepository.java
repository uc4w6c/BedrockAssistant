package com.github.uc4w6c.bedrockassistant.repository;

import com.github.uc4w6c.bedrockassistant.dao.ProfileDao;
import com.github.uc4w6c.bedrockassistant.domain.AwsProfile;
import software.amazon.awssdk.profiles.Profile;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileRepository {
  private final ProfileDao profileDao;

  public ProfileRepository() {
    profileDao = new ProfileDao();
  }

  public Set<AwsProfile> getProfiles() {
    return profileDao.getProfiles()
        .entrySet()
        .stream()
        .map(entry ->
            new AwsProfile(
                entry.getKey(),
                entry.getValue().property("region"),
                entry.getValue().property("role-arn"),
                entry.getValue().property("mfa-serial")))
        .collect(Collectors.toSet());
  }

  public Optional<AwsProfile> getProfile(String profileName) {
    Optional<Profile> optionalProfile = profileDao.getProfile(profileName);
    return optionalProfile.map(profile -> new AwsProfile(profile.name(),
        profile.property("region"),
        profile.property("role_arn"),
        profile.property("mfa_serial")));
  }
}
