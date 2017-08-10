package com.teammerge.services.impl;

import java.io.File;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.model.CreateBranchOptions;
import com.teammerge.model.GitOptions;
import com.teammerge.services.GitService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

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


    if (isCredentialsProvided(options.getUsername(), options.getPassword())) {
      cmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(options.getUsername(),
          options.getPassword()));
    }


    Git git = cmd.call();

    if (isDebugOn()) {
      LOG.debug("Repo " + options.getURI() + " cloned to " + options.getDestinationDirectory()
          + " in " + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }
    return git;
  }

  private boolean isCredentialsProvided(String username, String password) {
    return !StringUtils.isEmpty(username) && !StringUtils.isEmpty(password);
  }

  public Ref createBranch(CreateBranchOptions branchOptions) throws Exception {
    Ref ref = null;
    String startingPoint = branchOptions.getStartingPoint();
    try (Git git = new Git(branchOptions.getRepo())) {

      if (StringUtils.isEmpty(startingPoint)) {
        startingPoint = JGitUtils.getHEADRef(branchOptions.getRepo());
      }
      ref = git.branchCreate().setName(branchOptions.getBranchName()).call();
      if (ref == null) {
        LOG.debug("Trying to create a branch with starting point as master");
        ref =
            git.branchCreate().setName(branchOptions.getBranchName()).setStartPoint("master")
                .call();
      }
      if (ref == null) {
        LOG.debug("Trying to create a branch with starting point as origin/master");
        ref =
            git.branchCreate().setName(branchOptions.getBranchName())
                .setStartPoint("origin/master").call();
      }

      if (ref != null) {
        PushCommand pushCommand = git.push();
        pushCommand.setRemote(branchOptions.getRemoteURL());
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(branchOptions
            .getUserName(), branchOptions.getPassword()));
        pushCommand.add(ref);
        pushCommand.call();
      }
    }

    return ref;
  }
}
