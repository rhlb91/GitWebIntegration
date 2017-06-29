package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomTicketModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CustomizeService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.RefLogUtils;
import com.teammerge.utils.StringUtils;



@Service("customizeService")
public class CustomizeServiceImpl implements CustomizeService {


  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  public CustomTicketModel getDetailsForBranchName(String branchName) {
    CustomTicketModel customTicketModel = new CustomTicketModel();

    int numOfMatchedBranches = 0;
    List<CommitModel> commits = new ArrayList<>();

    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    Calendar c = Calendar.getInstance();
    c.setTime(new Date(0));
    Date minimumDate = c.getTime();
    TimeZone timezone = c.getTimeZone();

    for (RepositoryModel model : repositories) {

      if (model.isCollectingGarbage()) {
        continue;
      }

      Repository repository =
          repositoryService.getRepositoryManager().getRepository(model.getName());
      List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

      for (RefModel branch : branchModels) {
        if (branch.getName().contains(branchName)) {
          ++numOfMatchedBranches;

          if (model.isHasCommits()) {

            if (repository != null) {
              List<DailyLogEntry> dailyLogEntries =
                  RefLogUtils.getDailyLogByRef(model.getName(), repository, minimumDate, timezone);

              for (DailyLogEntry dailyLogEntry : dailyLogEntries) {
                if (dailyLogEntry.getCommitCount() >= 1) {
                  commits.addAll(populateCommits(dailyLogEntry.getCommits(), dailyLogEntry));
                }
              }
            }
          }
        }
      }

      customTicketModel.setNumOfBranches(numOfMatchedBranches);
      customTicketModel.setNumOfCommits(commits.size());
      customTicketModel.setCommits(commits);
      customTicketModel.setTicketId(branchName);
      
      return customTicketModel;
    }
    return null;
  }

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits, DailyLogEntry change) {
    List<CommitModel> populatedCommits = new ArrayList<>();

    for (RepositoryCommit commit : commits) {
      CommitModel commitModel = new CommitModel();
      commitModel.setCommitAuthor(commit.getAuthorIdent());

      // short message
      String shortMessage = commit.getShortMessage();
      String trimmedMessage = shortMessage;
      if (commit.getRefs() != null && commit.getRefs().size() > 0) {
        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG_REFS);
      } else {
        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG);
      }

      commitModel.setShortMessage(shortMessage);
      commitModel.setTrimmedMessage(trimmedMessage);

      // commit hash link
      int hashLen = 6;
      if (commit.getName() != null) {
        commitModel.setCommitHash(commit.getName().substring(0, hashLen));
      }
      commitModel.setName(commit.getName());

      if (commitModel.getShortMessage().startsWith("Merge")) {
        commitModel.setIsMergeCommit(true);
      } else {
        commitModel.setIsMergeCommit(false);
      }

      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }

}
