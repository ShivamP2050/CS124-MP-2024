package edu.illinois.cs.cs124.ay2024.mp.models;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RSO extends Summary {
  @NonNull
  private final String mission;

  @NonNull
  public String getMission() {
    return mission;
  }

  @NonNull
  private final String website;

  @NonNull
  public String getWebsite() {
    return website;
  }

  @NonNull
  private final List<String> categories;

  @NonNull
  public List<String> getCategories() {
    return categories;
  }

  public RSO(@NonNull RSOData rsoData) {
    super(rsoData.id(), rsoData.title(), new Summary(rsoData).getColor());
    website = rsoData.website();
    mission = rsoData.mission();

    String[] categoryParts = rsoData.categories().split("-");
    if (categoryParts.length == 0) {
      throw new IllegalStateException("Category not set for RSO");
    }
    String categoryPrefix = categoryParts[0].trim();
    categories = new ArrayList<String>();
    if  (categoryParts.length == 2) {
      categoryPrefix = categoryParts[1].trim();
      String[] catParts = categoryParts[1].split(",");
      for (String category : catParts) {
        categories.add(category.trim());
      }
    }
  }

  @JsonCreator
  public RSO(@NonNull String setId,
             @NonNull String setTitle,
             @NonNull Color setColor,
             @NonNull String setMission,
             @NonNull String setWebsite,
             @NonNull List<String> setCategories) {
    super(setId, setTitle, setColor);
    mission = setMission;
    website = setWebsite;
    categories = setCategories;
  }

  public List<Summary> getRelatedRSOs() {
    return Collections.emptyList();
  }

}
