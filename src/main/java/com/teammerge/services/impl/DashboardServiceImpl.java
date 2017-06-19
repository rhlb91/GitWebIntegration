package com.teammerge.services.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import com.teammerge.Constants;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.RefLogUtils;
import com.teammerge.utils.StringUtils;

public class DashboardServiceImpl implements DashBoardService {
  RepositoryService  repositoryService = new RepositoryServiceImpl();
  RuntimeServiceImpl runtimeService    = new RuntimeServiceImpl();

  public List<DailyLogEntry> getRawActivities() {

    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    int daysBack = 2 * 365; // 2 years, basically getting all activities
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, -1 * daysBack);
    Date minimumDate = c.getTime();
    TimeZone timezone = c.getTimeZone();

    // create daily commit digest feed
    List<DailyLogEntry> digests = new ArrayList<DailyLogEntry>();
    for (RepositoryModel model : repositories) {
      if (model.isCollectingGarbage()) {
        continue;
      }
      if (model.isHasCommits() && model.getLastChange().after(minimumDate)) {
        Repository repository = repositoryService.getRepositoryManager().getRepository(model.getName());

        if (repository != null) {
          List<DailyLogEntry> entries =
              RefLogUtils.getDailyLogByRef(model.getName(), repository, minimumDate, timezone);
          digests.addAll(entries);

          repository.close();
        } else {
          System.out.println("Repository " + model.getName() + " is null!!");
        }
      } else {
        System.out.println("Either repository " + model.getName()
            + " not has commits or there are no new changes after !!" + model.getLastChange());
      }
    }
    return digests;
  }

  public List<ActivityModel> populateActivities() {
    List<ActivityModel> activityModels = new ArrayList<>();
    List<DailyLogEntry> activities = getRawActivities();

    if (CollectionUtils.isNotEmpty(activities)) {
      for (DailyLogEntry dailyLogEntry : activities) {
        ActivityModel activityModel = new ActivityModel();
        final DailyLogEntry change = dailyLogEntry;

        Date pushDate = change.date;
        activityModel.setPushDate(pushDate);
        String color = StringUtils.getColor(StringUtils.stripDotGit(change.repository));

        activityModel.setColor(color);

        String fullRefName = change.getChangedRefs().get(0);
        String shortRefName = fullRefName;
        String ticketId = "";
        boolean isTag = false;
        boolean isTicket = false;

        if (shortRefName.startsWith(Constants.R_TICKET)) {
          ticketId = shortRefName = shortRefName.substring(Constants.R_TICKET.length());
          shortRefName = MessageFormat.format("ticket #{0}", ticketId);
          isTicket = true;
        } else if (shortRefName.startsWith(Constants.R_HEADS)) {
          shortRefName = shortRefName.substring(Constants.R_HEADS.length());
        } else if (shortRefName.startsWith(Constants.R_TAGS)) {
          shortRefName = shortRefName.substring(Constants.R_TAGS.length());
          isTag = true;
        }

        String whoChanged = "";
        if (isTag) {
          // tags are special
          PersonIdent ident = change.getCommits().get(0).getAuthorIdent();
          if (!StringUtils.isEmpty(ident.getName())) {
            whoChanged = ident.getName();
          } else {
            whoChanged = ident.getEmailAddress();
          }
        }

        String preposition = "gb.of";
        boolean isDelete = false;
        String what;
        String by = null;
        switch (change.getChangeType(fullRefName)) {
          case CREATE:
            if (isTag) {
              // new tag
              what = "created new tag";
              preposition = "gb.in";
            } else {
              // new branch
              what = "created new branch";
              preposition = "gb.in";
            }
            break;
          case DELETE:
            isDelete = true;
            if (isTag) {
              what = "deleted tag";
            } else {
              what = "deleted branch";
            }
            preposition = "gb.from";
            break;
          default:
            what =
                MessageFormat.format(
                    change.getCommitCount() > 1 ? "{0} commits to" : "1 commit to",
                    change.getCommitCount());

            if (change.getAuthorCount() == 1) {
              by = MessageFormat.format("by {0}", change.getAuthorIdent().getName());
            } else {
              by = MessageFormat.format("by {0} authors", change.getAuthorCount());
            }
            break;
        }

        String repoName = StringUtils.stripDotGit(change.repository);

        int maxCommitCount = 5;
        List<RepositoryCommit> commits = change.getCommits();
        if (commits.size() > maxCommitCount) {
          commits = new ArrayList<RepositoryCommit>(commits.subList(0, maxCommitCount));
        }

        activityModel.setFullRefName(fullRefName);
        activityModel.setShortRefName(shortRefName);
        activityModel.setTicket(isTicket);
        activityModel.setTag(isTag);
        activityModel.setTicketId(ticketId);
        activityModel.setWhoChanged(whoChanged);
        activityModel.setWhatChanged(what);
        activityModel.setPreposition(preposition); // to/from/etc
        activityModel.setByAuthor(by);
        activityModel.setRepositoryName(repoName);
        activityModel.setCommits(populateCommits(commits, change));

        activityModels.add(activityModel);
      }
    }

    return activityModels;
  }

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits, DailyLogEntry change) {
    List<CommitModel> populatedCommits = new ArrayList<>();

    for (RepositoryCommit commit : commits) {
      CommitModel commitModel = new CommitModel();

      // author gravatar
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

      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }
}
