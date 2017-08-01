package com.teammerge.strategy.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teammerge.strategy.CommitDiffStrategy;
import com.teammerge.utils.StringUtils;

public class CommitDiffStrategyWithByteStream implements CommitDiffStrategy {

  private final static Logger LOG = LoggerFactory.getLogger(CommitDiffStrategyWithByteStream.class);

  @Override
  public List<String> getCommitDif(Repository r, String baseCommitId, String commitId)
      throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException,
      IOException {


    if (StringUtils.isEmpty(commitId)) {
      LOG.error("Cannot find commit diff!! Provided commitId is null");
      return null;
    }

    RevCommit commit = null;
    try (RevWalk walk = new RevWalk(r)) {
      commit = walk.parseCommit(r.resolve(commitId));
      walk.dispose();
    }

    ObjectReader reader = r.newObjectReader();
    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
    ObjectId newTree = null;

    if (commit != null) {
      newTree = commit.getTree();
    } else {
      newTree = r.resolve(commitId + "^{tree}");
    }
    newTreeIter.reset(reader, newTree);

    if (commit.getParentCount() < 1) {
      LOG.error("Cannot find commit diff!! Provided commitId does not have a parent");
      return null;
    }

    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
    //ObjectId oldTree = commit.getParent(0).getTree(); // equals oldCommit.getTree()
    ObjectId oldTree = r.resolve(commit.getParent(0).getName() + "^{tree}");
    oldTreeIter.reset(reader, oldTree);

    int linesDeleted = 0, linesAdded = 0;
    List<String> diffText = new ArrayList<String>();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (DiffFormatter diffFormatter = new DiffFormatter(out)) {
      diffFormatter.setRepository(r);
      for (DiffEntry entry : diffFormatter.scan(oldTreeIter, newTreeIter)) {
        try {
          // Format a patch script for one file entry.
          diffFormatter.format(entry);

          diffText.add(out.toString());
          out.reset();
        } catch (IOException e) {
          e.printStackTrace();
        }

        for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
          linesDeleted += edit.getEndA() - edit.getBeginA();
          linesAdded += edit.getEndB() - edit.getBeginB();
        }
        System.out.println("\nlinesDeleted:" + linesDeleted);
        System.out.println("linesAdded:" + linesAdded);
      }
    }
    return diffText;
  }

}
