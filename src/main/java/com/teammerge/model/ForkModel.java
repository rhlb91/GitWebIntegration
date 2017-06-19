package com.teammerge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.teammerge.utils.ArrayUtils;
import com.teammerge.utils.StringUtils;

/**
 * A ForkModel represents a repository, its direct descendants, and its origin.
 *
 *
 */
public class ForkModel implements Serializable {

  private static final long    serialVersionUID = 1L;

  public final RepositoryModel repository;

  public final List<ForkModel> forks;

  public ForkModel(RepositoryModel repository) {
    this.repository = repository;
    this.forks = new ArrayList<ForkModel>();
  }

  public boolean isRoot() {
    return StringUtils.isEmpty(repository.getOriginRepository());
  }

  public boolean isNode() {
    return !ArrayUtils.isEmpty(forks);
  }

  public boolean isLeaf() {
    return ArrayUtils.isEmpty(forks);
  }

  public boolean isPersonalRepository() {
    return repository.isPersonalRepository();
  }

  @Override
  public int hashCode() {
    return repository.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ForkModel) {
      return repository.equals(((ForkModel) o).repository);
    }
    return false;
  }

  @Override
  public String toString() {
    return repository.toString();
  }
}
