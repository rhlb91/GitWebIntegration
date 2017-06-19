package com.teammerge.model;

import java.io.Serializable;

/**
 * SubmoduleModel is a serializable model class that represents a git submodule definition.
 *
 *
 */
public class SubmoduleModel implements Serializable {

  private static final long serialVersionUID = 1L;

  public final String       name;
  public final String       path;
  public final String       url;

  public boolean            hasSubmodule;
  public String             gitblitPath;

  public SubmoduleModel(String name, String path, String url) {
    this.name = name;
    this.path = path;
    this.url = url;
  }

  @Override
  public String toString() {
    return path + "=" + url;
  }
}
