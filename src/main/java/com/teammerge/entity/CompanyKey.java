package com.teammerge.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;

public class CompanyKey implements Serializable {

  private static final long serialVersionUID = 8909302378224004127L;

  @Column(name = "companyName", unique = true, nullable = false)
  public String name;

  @Column(name = "projectName", unique = true, nullable = false)
  public String projectName;

  public CompanyKey() {
    this(null, null);
  }

  public CompanyKey(String cName, String pName) {
    this.name = cName;
    this.projectName = pName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getProjectName());
  }

  @Override
  public boolean equals(Object o) {

    if (o == this)
      return true;
    if (!(o instanceof CompanyKey)) {
      return false;
    }
    CompanyKey key = (CompanyKey) o;
    return Objects.equals(getName(), key.getName())
        && Objects.equals(getProjectName(), key.getProjectName());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

}
