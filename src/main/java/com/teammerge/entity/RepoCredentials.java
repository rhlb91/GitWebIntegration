package com.teammerge.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "repo_credentials")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "entity")
public class RepoCredentials implements java.io.Serializable {

  private static final long serialVersionUID = -6344513471738441090L;

  @EmbeddedId
  private RepoCredentialsKey repoCredentialsKey;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password", nullable = false)
  private String password;

  public String getCompany() {
    if (repoCredentialsKey != null)
      return repoCredentialsKey.getCompany();
    else
      return null;
  }

  public void setCompany(String company) {
    if (repoCredentialsKey == null) {
      this.repoCredentialsKey = new RepoCredentialsKey();
    }
    this.repoCredentialsKey.setCompany(company);
  }

  public String getRepoName() {
    if (repoCredentialsKey != null)
      return repoCredentialsKey.getRepoName();
    else
      return null;
  }

  public void setRepoName(String repoName) {
    if (this.repoCredentialsKey == null) {
      this.repoCredentialsKey = new RepoCredentialsKey();
    }
    this.repoCredentialsKey.setRepoName(repoName);
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


}
