package edu.illinois.cs.cs124.ay2024.mp.models;

import androidx.annotation.NonNull;

public class Favorite {

  @NonNull
  private final String id;

  private final boolean favorite;

  public boolean getFavorite() {
    return favorite;
  }

  public Favorite(@NonNull String setId, boolean setFavorite) {
    id = setId;
    favorite = setFavorite;
  }

  public String getId() {
    return id;
  }
}
