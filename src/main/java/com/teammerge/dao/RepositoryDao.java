package com.teammerge.dao;

import java.util.List;

import com.teammerge.model.RepositoryModel;

public interface RepositoryDao extends BaseDao<RepositoryModel> {

  List<RepositoryModel> fetchRepositoriesForCompany(final String companyName);

  List<RepositoryModel> fetchAllRepositories();

  List<String> fetchAllRepositoryNames();

}
