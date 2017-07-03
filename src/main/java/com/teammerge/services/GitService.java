package com.teammerge.services;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.teammerge.model.GitOptions;

public interface GitService {
  Git cloneRepository(GitOptions options) throws InvalidRemoteException, TransportException, GitAPIException;
  
  
}
