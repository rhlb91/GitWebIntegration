package com.teammerge.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.manager.RepositoryManager;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.RepositoryService;

@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {

  private static IRepositoryManager repositoryManager = null;
  
  @Value("${git.repositoriesFolderPlaceholder}")
  private String repositoriesFolderPath;
  
  @Autowired
  private RuntimeServiceImpl runtimeService;

  public IRepositoryManager getRepositoryManager() {

    if (repositoryManager == null) {
      repositoryManager = new RepositoryManager(runtimeService.getRuntimeManager(), null,repositoriesFolderPath);
    }

    return repositoryManager;
  }

  public List<RepositoryModel> getRepositoryModels() {
    return getRepositoryManager().getRepositoryModels();
  }

}
