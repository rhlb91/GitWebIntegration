package com.teammerge.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "company")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ReadWriteRegion")
public class Company implements java.io.Serializable {

  private static final long serialVersionUID = 7713761242982086415L;

  @EmbeddedId
  private CompanyKey companyKey;

  @Column(name = "remoteURL", nullable = false)
  private String remoteURL;

  @Column(name = "status")
  private String status;

  public String getName() {
    if (this.companyKey != null) {
      return this.companyKey.getName();
    }
    return null;
  }

  public void setName(String name) {
    if (this.companyKey == null) {
      this.companyKey = new CompanyKey();
    }
    this.companyKey.setName(name);
  }

  public String getProjectName() {
    if (this.companyKey != null) {
      return this.companyKey.getProjectName();
    }
    return null;
  }

  public void setProjectName(String projectName) {
    if (this.companyKey == null) {
      this.companyKey = new CompanyKey();
    }
    this.companyKey.setProjectName(projectName);
  }

  public String getRemoteURL() {
    return remoteURL;
  }

  public void setRemoteURL(String remoteURL) {
    this.remoteURL = remoteURL;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
