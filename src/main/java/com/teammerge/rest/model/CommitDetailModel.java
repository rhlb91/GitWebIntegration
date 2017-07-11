package com.teammerge.rest.model;

import java.util.Date;

import org.eclipse.jgit.lib.PersonIdent;

public class CommitDetailModel {

  private PersonIdent commitAuthor;
  private String commitshortMessage;
  private String commitHash;
  private Date commitDate;
  private String branchId;
}
