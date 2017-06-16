package com.teammerge.services.impl;

import java.util.List;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.manager.RepositoryManager;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.RepositoryService;
import com.teammerge.services.RuntimeService;

public class RepositoryServiceImpl implements RepositoryService {

	private static IRepositoryManager repositoryManager = null;
	private RuntimeService runtimeService = new RuntimeService();

	public IRepositoryManager getRepositoryManager() {

		if (repositoryManager == null) {
			repositoryManager = new RepositoryManager(
					runtimeService.getRuntimeManager(), null);
		}

		return repositoryManager;
	}

	public List<RepositoryModel> getRepositoryModels() {
		return getRepositoryManager().getRepositoryModels();
	}

}
