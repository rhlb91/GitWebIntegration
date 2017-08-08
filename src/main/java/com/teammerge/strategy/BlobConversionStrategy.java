package com.teammerge.strategy;

import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.teammerge.IStoredSettings;

public interface BlobConversionStrategy {

  Map<String, Object> convert(String blobPath, Repository r, RevCommit c, IStoredSettings s);

  public enum Key {
    SOURCE, FILE_EXTENSION;
  }
}
