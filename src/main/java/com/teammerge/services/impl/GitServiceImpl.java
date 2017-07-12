package com.teammerge.services.impl;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.model.GitOptions;
import com.teammerge.services.GitService;
import com.teammerge.utils.LoggerUtils;

@Service("gitService")
public class GitServiceImpl implements GitService {
  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public Git cloneRepository(GitOptions options) throws InvalidRemoteException, TransportException,
      GitAPIException {
    long start = System.currentTimeMillis();
    LOG.info("Cloning repo " + options.getURI());

    CloneCommand cmd = Git.cloneRepository();
    cmd.setCloneAllBranches(options.isCloneAllBranches());
    cmd.setCloneSubmodules(options.isIncludeSubModule());
    cmd.setURI(options.getURI());
    cmd.setBare(options.isBare());
    cmd.setNoCheckout(Boolean.TRUE);
    File destinationFolder = new File(options.getDestinationDirectory());
    cmd.setDirectory(destinationFolder);
    Git git = cmd.call();

    if (isDebugOn()) {
      LOG.debug("Repo " + options.getURI() + " cloned to "
          + options.getDestinationDirectory() + " in "
          + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }
    return git;
  }
}
