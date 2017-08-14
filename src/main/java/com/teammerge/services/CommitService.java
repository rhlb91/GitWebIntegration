package com.teammerge.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;

import com.teammerge.entity.CommitModel;
import com.teammerge.form.CommitForm;

public interface CommitService {

  public Map<String, List<CommitModel>> getDetailsForBranchName(String branchName);

  public List<CommitModel> getCommitDetailsAll();

  public List<CommitModel> getCommitDetails(String commitId);

  void saveCommit(CommitModel commit);

  void saveOrUpdateCommitDetails(final CommitForm commitForm);

  /**
   * Generates difference between 2 commits.
   * 
   * @param repoName
   * @param branch
   * @param commitId
   * @return
   * @throws RevisionSyntaxException
   * @throws AmbiguousObjectException
   * @throws IncorrectObjectTypeException
   * @throws IOException
   */
  List<String> getCommitDiff(String repoName, String branch, String commitId)
      throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException,
      IOException;

  void saveOrUpdateCommit(CommitModel commit);

  void saveOrUpdateCommitInSeparateSession(CommitModel commit);
}
