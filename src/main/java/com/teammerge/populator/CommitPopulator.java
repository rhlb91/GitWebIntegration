package com.teammerge.populator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.Constants;
import com.teammerge.GitWebException;
import com.teammerge.entity.CommitModel;
import com.teammerge.form.CommitForm;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.utils.StringUtils;
import com.teammerge.utils.TimeUtils;
import org.eclipse.jgit.lib.PersonIdent;
@Component
public class CommitPopulator {

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  public void populate(RepositoryCommit source1, CustomRefModel source2, CommitModel target)
      throws GitWebException.InvalidArgumentsException {

    if (source1 == null || source2 == null || target == null) {
      throw new GitWebException.InvalidArgumentsException("One of the parameters null");
    }

    target.setCommitAuthor(source1.getAuthorIdent());
    // short message
    String shortMessage = source1.getShortMessage();
    String trimmedMessage = shortMessage;
    if (source1.getRefs() != null && source1.getRefs().size() > 0) {
      trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG_REFS);
    } else {
      trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG);
    }
    target.setShortMessage(shortMessage);
    target.setTrimmedMessage(trimmedMessage);

    // commit hash link
    int hashLen = 6;
    if (source1.getName() != null) {
      target.setCommitHash(source1.getName().substring(0, hashLen));
    }
    target.setCommitId(source1.getName());
    if (target.getShortMessage().startsWith("Merge")) {
      target.setIsMergeCommit(true);
    } else {
      target.setIsMergeCommit(false);
    }

    target.setCommitDate(source1.getCommitDate());
    target.setCommitTimeFormatted(TimeUtils.convertToDateFormat(source1.getCommitDate(),
        commitTimeFormat));

    target.setBranchName(source2.getRefModel().getName());
    target.setRepositoryName(source2.getRepositoryName());
  }
  
  public void populate(CommitForm source, CommitModel target) {
    target.setCommitId(source.getCommitId());
    target.setCommitAuthor(new PersonIdent(source.getAuthorName(), source.getAuthorEmail(),
      Long.valueOf(source.getWhen()), Integer.valueOf(source.getTimezone())));
    target.setBranchName(source.getBranchName());
    target.setCommitDate(TimeUtils.convertToDateFormat(Long.valueOf(source.getCommitDate())));
    target.setCommitHash(source.getCommitHash());
    target.setCommitTimeFormatted(source.getFormattedTime());
    target.setIsMergeCommit(Boolean.valueOf(source.getIsMergeCommit()));
    target.setRepositoryName(source.getRepoName());
    target.setShortMessage(source.getShortMsg());
    target.setTrimmedMessage(source.getTrimmedMsg());
    }
 }
