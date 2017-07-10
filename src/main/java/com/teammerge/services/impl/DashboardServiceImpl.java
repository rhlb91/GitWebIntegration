package com.teammerge.services.impl;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.ObjectCache;
import com.teammerge.utils.RefLogUtils;
import com.teammerge.utils.StringUtils;

@Service("dashBoardService")
public class DashboardServiceImpl implements DashBoardService {
  private final Logger LOG = LoggerFactory.getLogger(DashboardServiceImpl.class);

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  @Value("${git.dashboard.dateFormat}")
  private String dateFormat;

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.debug}")
  private String debug;

  private static final ObjectCache<ActivityModel> activityCache = new ObjectCache<ActivityModel>();

  // private static Date lastActivityUpdated = new Date(0);
  private Map<RepositoryModel, Date> lastActivityPerRepo = new HashMap<>();

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Override
  public List<DailyLogEntry> getRawActivities(final int daysBack) {
    long start = System.currentTimeMillis();
    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    Date minimumDate = null;
    if (daysBack == -1) {
      minimumDate = TimeUtils.getInceptionDate();
    } else {
      minimumDate = TimeUtils.getDateInDaysBack(daysBack);
    }

    // create daily commit digest feed
    List<DailyLogEntry> digests = new ArrayList<DailyLogEntry>();
    for (RepositoryModel model : repositories) {
      if (model.isCollectingGarbage()) {
        continue;
      }
      if (model.isHasCommits() && model.getLastChange().after(minimumDate)) {
        Repository repository = repositoryService.getRepository(model.getName(), true);

        if (repository != null) {
          List<DailyLogEntry> entries =
              RefLogUtils.getDailyLogByRef(model.getName(), repository, minimumDate,
                  TimeUtils.getTimeZone());

          updateLastActivityInsertedPerRepo(model, entries);

          digests.addAll(entries);
          repository.close();
        } else {
          LOG.error("Repository " + model.getName() + " is null!!");
        }
      } else {
        LOG.warn("Either repository " + model.getName()
            + " not has commits or there are no new changes after " + model.getLastChange());
      }
    }

    if (isDebugOn()) {
      LOG.debug("Fetched raw activities in "
          + LoggerUtils.getTimeInSecs(start, System.currentTimeMillis()));
    }

    return digests;
  }

  private void updateLastActivityInsertedPerRepo(RepositoryModel model, List<DailyLogEntry> entries) {
    Date lastActivityDate = new Date(0);
    if (CollectionUtils.isNotEmpty(entries)) {
      lastActivityDate = entries.get(0).date;
    }
    lastActivityPerRepo.put(model, lastActivityDate);
  }

  @Override
  public List<ActivityModel> populateActivities(final boolean cached, final int daysBack) {
    List<ActivityModel> activityModels = new ArrayList<>();
    List<DailyLogEntry> rawActivities = getRawActivities(daysBack);
    ActivityModel activityModel = new ActivityModel();
    int cacheHit = 0;
    int cacheMiss = 0;
    long start = System.currentTimeMillis();

    if (CollectionUtils.isNotEmpty(rawActivities)) {
      for (DailyLogEntry dailyLogEntry : rawActivities) {
        if (cached) {
          if (activityCache.hasCurrent(getUniqueKeyForActivity(dailyLogEntry), dailyLogEntry.date)) {
            ++cacheHit;
            activityModel = activityCache.getObject(getUniqueKeyForActivity(dailyLogEntry));
          } else {
            ++cacheMiss;
            activityModel = populateActivity(dailyLogEntry);

            activityCache.updateObject(getUniqueKeyForActivity(dailyLogEntry), dailyLogEntry.date,
                activityModel);
          }
        }

        activityModels.add(activityModel);
      }
    }

    Comparator<ActivityModel> activitySort = new Comparator<ActivityModel>() {
      @Override
      public int compare(ActivityModel o1, ActivityModel o2) {
        return o2.getPushDate().compareTo(o1.getPushDate());
      }
    };

    Collections.sort(activityModels, activitySort);

    if (isDebugOn()) {
      LOG.debug("Total time taken to populate activities: "
          + ((System.currentTimeMillis() - start) / 1000.0) + " secs");
      LOG.debug("Total activities: " + activityModels.size());
      LOG.debug("Cache Hits: " + cacheHit + ", Cache Miss: " + cacheMiss);
    }
    return activityModels;
  }

  private String getUniqueKeyForActivity(DailyLogEntry dailyLogEntry) {
    String str = String.valueOf(dailyLogEntry.date.getTime());
    if (dailyLogEntry.getAuthorIdent() != null) {
      str += "_" + dailyLogEntry.getAuthorIdent().getName();
    }
    return str;
  }

  private ActivityModel populateActivity(DailyLogEntry change) {
    ActivityModel activityModel = new ActivityModel();

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
            MessageFormat.format(change.getCommitCount() > 1 ? "{0} commits to" : "1 commit to",
                change.getCommitCount());

        if (change.getAuthorCount() == 1) {
          by = MessageFormat.format("by {0}", change.getAuthorIdent().getName());
        } else {
          by = MessageFormat.format("by {0} authors", change.getAuthorCount());
        }
        break;
    }

    String repoName = StringUtils.stripDotGit(change.repository);

    int maxCommitCount = 20;
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
    activityModel.setWhenChanged(getWhenChanged(change.date));
    activityModel.setCommits(populateCommits(commits));

    return activityModel;
  }

  private String getWhenChanged(Date date) {
    String fuzzydate;
    Calendar c = Calendar.getInstance();
    TimeZone timezone = c.getTimeZone();
    TimeUtils tu = new TimeUtils(timezone);

    DateFormat df = new SimpleDateFormat(dateFormat);
    df.setTimeZone(timezone);

    Date pushDate = date;
    if (TimeUtils.isToday(pushDate, timezone)) {
      fuzzydate = tu.today();
    } else if (TimeUtils.isYesterday(pushDate, timezone)) {
      fuzzydate = tu.yesterday();
    } else {
      fuzzydate = tu.timeAgo(pushDate);
    }
    return fuzzydate + ", " + df.format(pushDate);
  }

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits) {
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

      if (commitModel.getShortMessage().startsWith("Merge")) {
        commitModel.setIsMergeCommit(true);
      } else {
        commitModel.setIsMergeCommit(false);
      }

      commitModel.setCommitDate(commit.getCommitDate());
      commitModel.setCommitTimeFormatted(TimeUtils.convertToDateFormat(commit.getCommitDate(),
          commitTimeFormat));

      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }


}
