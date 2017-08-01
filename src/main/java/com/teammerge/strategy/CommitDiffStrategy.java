package com.teammerge.strategy;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Repository;

import com.teammerge.model.RepositoryCommit;

public interface CommitDiffStrategy {

  /**
   * <p>
   * If the baseCommit is null, than the commit's parent is calculated and difference is shown
   * between commit and its parent commit
   * </p>
   * 
   * @param r repository
   * @param baseCommit commit1
   * @param commit commit2
   * @return
   * @throws RevisionSyntaxException
   * @throws AmbiguousObjectException
   * @throws IncorrectObjectTypeException
   * @throws IOException
   */
  List<String> getCommitDif(final Repository r, String baseCommitId, String commitId)
      throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException,
      IOException;
}
