package com.teammerge.model;

import org.eclipse.jgit.lib.Repository;

public class CustomRefModel {

  private Repository repository;
  private String repositoryName;
  private RefModel refModel;

  public CustomRefModel() {

  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public String getRepositoryName() {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }

  public RefModel getRefModel() {
    return refModel;
  }

  public void setRefModel(RefModel refModel) {
    this.refModel = refModel;
  }

}
