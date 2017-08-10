package com.teammerge.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;

import com.teammerge.model.CreateBranchOptions;
import com.teammerge.model.GitOptions;

public interface GitService {

  Git cloneRepository(GitOptions options) throws InvalidRemoteException, TransportException,
      GitAPIException;

  Ref createBranch(CreateBranchOptions branchOptions) throws GitAPIException, Exception;

}
