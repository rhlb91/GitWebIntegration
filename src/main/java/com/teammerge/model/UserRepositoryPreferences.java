package com.teammerge.model;

import java.io.Serializable;

/**
 * User repository preferences.
 *
 *
 */
public class UserRepositoryPreferences implements Serializable {

  private static final long serialVersionUID = 1L;

  public String username;

  public String repositoryName;

  public boolean starred;

  @Override
  public String toString() {
    return username + ":" + repositoryName;
  }
}
