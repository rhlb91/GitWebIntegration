package com.teammerge.services.impl;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.teammerge.model.GitOptions;
import com.teammerge.services.GitService;

@Service("gitService")
public class GitServiceImpl implements GitService {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public Git cloneRepository(GitOptions options) throws InvalidRemoteException, TransportException,
      GitAPIException {

    logger.info("Cloning repo " + options.getDestinationDirectory());

    CloneCommand cmd = Git.cloneRepository();
    cmd.setCloneAllBranches(options.isCloneAllBranches());
    cmd.setCloneSubmodules(options.isIncludeSubModule());
    cmd.setURI(options.getURI());

    File destinationFolder = new File(options.getDestinationDirectory());
    cmd.setDirectory(destinationFolder);

    return cmd.call();
  }
}
