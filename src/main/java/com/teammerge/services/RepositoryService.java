package com.teammerge.services;

import java.util.List;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.model.RepositoryModel;

public interface RepositoryService {
	IRepositoryManager getRepositoryManager();

	List<RepositoryModel> getRepositoryModels();
}
