package com.teammerge.model;

import com.teammerge.manager.IRuntimeManager;
import com.teammerge.manager.IUserManager;

public class RepoParams {
  private IRuntimeManager runtimeManager;
  private IUserManager userManager;
  private String repoFolderName;
  private String remoteRepoPath;

  public IRuntimeManager getRuntimeManager() {
    return runtimeManager;
  }

  public void setRuntimeManager(IRuntimeManager runtimeManager) {
    this.runtimeManager = runtimeManager;
  }

  public IUserManager getUserManager() {
    return userManager;
  }

  public void setUserManager(IUserManager userManager) {
    this.userManager = userManager;
  }

  public String getRepoFolderName() {
    return repoFolderName;
  }

  public void setRepoFolderName(String repoFolderName) {
    this.repoFolderName = repoFolderName;
  }

  public String getRemoteRepoPath() {
    return remoteRepoPath;
  }

  public void setRemoteRepoPath(String remoteRepoPath) {
    this.remoteRepoPath = remoteRepoPath;
  }

}
