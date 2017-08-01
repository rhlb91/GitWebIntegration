package com.teammerge.model;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Model class to represent a RevCommit, it's source repository, and the branch. This class is used
 * by the activity page.
 *
 */
public class RepositoryCommit implements Serializable, Comparable<RepositoryCommit> {

  private static final long serialVersionUID = 1L;

  public final String repository;

  public final String branch;

  private final RevCommit commit;

  private List<RefModel> refs;

  public RepositoryCommit(String repository, String branch, RevCommit commit) {
    this.repository = repository;
    this.branch = branch;
    this.commit = commit;
  }

  public void setRefs(List<RefModel> refs) {
    this.refs = refs;
  }

  public List<RefModel> getRefs() {
    return refs;
  }

  public ObjectId getId() {
    return getCommit().getId();
  }

  public String getName() {
    return getCommit().getName();
  }

  public String getShortName() {
    return getCommit().getName().substring(0, 8);
  }

  public String getShortMessage() {
    return getCommit().getShortMessage();
  }

  public Date getCommitDate() {
    return new Date(getCommit().getCommitTime() * 1000L);
  }

  public int getParentCount() {
    return getCommit().getParentCount();
  }

  public RevCommit[] getParents() {
    return getCommit().getParents();
  }

  public PersonIdent getAuthorIdent() {
    return getCommit().getAuthorIdent();
  }

  public PersonIdent getCommitterIdent() {
    return getCommit().getCommitterIdent();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof RepositoryCommit) {
      RepositoryCommit commit = (RepositoryCommit) o;
      return repository.equals(commit.repository) && getName().equals(commit.getName());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (repository + getCommit()).hashCode();
  }

  @Override
  public int compareTo(RepositoryCommit o) {
    // reverse-chronological order
    if (getCommit().getCommitTime() > o.getCommit().getCommitTime()) {
      return -1;
    } else if (getCommit().getCommitTime() < o.getCommit().getCommitTime()) {
      return 1;
    }
    return 0;
  }

  public RepositoryCommit clone(String withRef) {
    return new RepositoryCommit(repository, withRef, getCommit());
  }

  @Override
  public String toString() {
    return MessageFormat.format("{0} {1} {2,date,yyyy-MM-dd HH:mm} {3} {4}", getShortName(),
        branch, getCommitterIdent().getWhen(), getAuthorIdent().getName(), getShortMessage());
  }

  public RevCommit getCommit() {
    return commit;
  }
}
