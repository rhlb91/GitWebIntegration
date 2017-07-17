package com.teammerge.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class Company implements java.io.Serializable {

  private static final long serialVersionUID = 6705919882734212383L;

  @Id
  @Column(name = "name", unique = true, nullable = false)
  private String name;
  @ElementCollection(targetClass=String.class)
  @Column(name = "ownedRepositories")
  @ElementCollection(targetClass=String.class)
  private List<String> ownedRepositories;

  @ElementCollection(targetClass = String.class) 
  @MapKeyClass(String.class) 
  @Column(name = "remoteRepoUrls")
  private Map<String, String> remoteRepoUrls;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getOwnedRepositories() {
    return ownedRepositories;
  }

  public void setOwnedRepositories(List<String> ownedRepositories) {
    this.ownedRepositories = ownedRepositories;
  }

  public Map<String, String> getRemoteRepoUrls() {
    return remoteRepoUrls;
  }

  public void setRemoteRepoUrls(Map<String, String> remoteRepoUrls) {
    this.remoteRepoUrls = remoteRepoUrls;
  }


}