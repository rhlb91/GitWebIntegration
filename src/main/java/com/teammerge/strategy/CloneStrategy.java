package com.teammerge.strategy;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

import com.teammerge.model.RepositoryModel;

public interface CloneStrategy {
  Repository createOrUpdateRepo(File f, String repoName, RepositoryModel repoModel, boolean isRepoExists);
}
