package com.teammerge.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class ActivityModel {
  private Date              pushDate;
  private String            color;
  private boolean           isTag;
  private boolean           isTicket;
  private String            fullRefName;
  private String            shortRefName;
  private String            whoChanged;
  private String            byAuthor;
  private String            whatChanged;
  private String            preposition;
  private String            ticketId;
  private List<CommitModel> commits;
  private String            repositoryName;

  @Override
  public String toString() {
    String str = "";
    str += "Repo Name: " + repositoryName;
    str += ", push Date: " + pushDate;
    str += ", who Changed: " + whoChanged;
    str += ", by Author: " + byAuthor;
    str += ", what Changed: " + whatChanged;
    str += ", ticket Id:" + ticketId;
    if (CollectionUtils.isNotEmpty(commits)) {
      str += ", commits: " + commits;
    }
    str += "<br><br>";
    return str;
  }

  public Date getPushDate() {
    return pushDate;
  }

  public void setPushDate(Date pushDate) {
    this.pushDate = pushDate;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public boolean isTag() {
    return isTag;
  }

  public void setTag(boolean isTag) {
    this.isTag = isTag;
  }

  public boolean isTicket() {
    return isTicket;
  }

  public void setTicket(boolean isTicket) {
    this.isTicket = isTicket;
  }

  public String getFullRefName() {
    return fullRefName;
  }

  public void setFullRefName(String fullRefName) {
    this.fullRefName = fullRefName;
  }

  public String getShortRefName() {
    return shortRefName;
  }

  public void setShortRefName(String shortRefName) {
    this.shortRefName = shortRefName;
  }

  public String getWhoChanged() {
    return whoChanged;
  }

  public void setWhoChanged(String whoChanged) {
    this.whoChanged = whoChanged;
  }

  public String getByAuthor() {
    return byAuthor;
  }

  public void setByAuthor(String by) {
    this.byAuthor = by;
  }

  public String getWhatChanged() {
    return whatChanged;
  }

  public void setWhatChanged(String what) {
    this.whatChanged = what;
  }

  public String getPreposition() {
    return preposition;
  }

  public void setPreposition(String preposition) {
    this.preposition = preposition;
  }

  public String getTicketId() {
    return ticketId;
  }

  public void setTicketId(String ticketId) {
    this.ticketId = ticketId;
  }

  public List<CommitModel> getCommits() {
    return commits;
  }

  public void setCommits(List<CommitModel> commits) {
    this.commits = commits;
  }

  public String getRepositoryName() {
    return repositoryName;
  }

  public void setRepositoryName(String repositoryName) {
    this.repositoryName = this.repositoryName;
  }

}
