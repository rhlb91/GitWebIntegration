package com.teammerge.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.teammerge.Constants.CloneStatus;
import com.teammerge.Constants.CloneStatus.RepoActiveStatus;

@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ReadWriteRegion")
public class Company implements java.io.Serializable {

  private static final long serialVersionUID = 6705919882734212383L;

  @Id
  @Column(name = "name", unique = true, nullable = false)
  public String name;

  /**
   * Map of ProjectId and Remote repo url<br>
   * <br>
   * E.g. [Teamerge -> https://123.124.11.1/teamerge.git ]
   */
  @Column(name = "remoteRepoUrls")
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  @MapKeyClass(String.class)
  private Map<String, String> remoteRepoUrls;


  /**
   * Map of ProjectId and Active/Inactive status  for repository<br>
   * <br>
   * E.g. [Teamerge -> Active/Inactive ]
   */

  @Column(name = "repoActiveStatus")
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  @MapKeyClass(String.class)
  private Map<String, String> repoStatuses;

  public Company() {
    this.name = null;
  }

  public Company(String name) {
    this.name = name;

  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getRemoteRepoUrls() {
    return remoteRepoUrls;
  }

  public void setRemoteRepoUrls(Map<String, String> remoteRepoUrls) {
    this.remoteRepoUrls = remoteRepoUrls;
  }

  public Map<String, String> getRepoStatuses() {
    return repoStatuses;
  }

  public void setRepoStatuses(Map<String, String> repoStatuses) {
    this.repoStatuses = repoStatuses;
  }


}

