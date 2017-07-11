package com.teammerge.strategy;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

public interface CloneStrategy {
  Repository createOrUpdateRepo(File f, String repoName, boolean isRepoExists);
}
