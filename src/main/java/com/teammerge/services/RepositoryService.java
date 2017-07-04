package com.teammerge.services;

import java.util.List;

import org.eclipse.jgit.lib.Repository;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.model.RepositoryModel;

public interface RepositoryService {
  IRepositoryManager getRepositoryManager();

  List<RepositoryModel> getRepositoryModels();

  Repository getRepository(String repositoryName,boolean updateRequired);

  List<String> getRepositoryList();

}
