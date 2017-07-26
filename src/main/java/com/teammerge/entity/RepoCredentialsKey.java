package com.teammerge.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;

public class RepoCredentialsKey implements Serializable {

  private static final long serialVersionUID = -9088889082727359327L;

  @Column(name = "company", nullable = false)
  private String company;

  @Column(name = "repoName", nullable = false)
  private String repoName;

  public RepoCredentialsKey() {}

  public RepoCredentialsKey(String company, String repoName) {
    this.company = company;
    this.repoName = repoName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(company, repoName);
  }

  @Override
  public boolean equals(Object o) {

    if (o == this)
      return true;
    if (!(o instanceof RepoCredentialsKey)) {
      return false;
    }
    RepoCredentialsKey key = (RepoCredentialsKey) o;
    return Objects.equals(repoName, key.repoName) && Objects.equals(company, key.company);
  }

  /**
   * @return the company
   */
  public String getCompany() {
    return company;
  }

  /**
   * @param company the company to set
   */
  public void setCompany(String company) {
    this.company = company;
  }

  /**
   * @return the repoName
   */
  public String getRepoName() {
    return repoName;
  }

  /**
   * @param repoName the repoName to set
   */
  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }


}
