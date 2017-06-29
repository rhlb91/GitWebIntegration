package com.teammerge.services.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomTicketModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RefLogEntry;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CustomizeService;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.ObjectCache;
import com.teammerge.utils.RefLogUtils;
import com.teammerge.utils.StringUtils;



@Service("customizeService")
public class CustomizeServiceImpl implements CustomizeService {
  
  private static final ObjectCache<CustomTicketModel> activityCache = new ObjectCache<CustomTicketModel>();

  private Map<RepositoryModel, Date> lastActivityPerRepo = new HashMap<>();
  @Resource(name = "repositoryService")
  RepositoryService repositoryService;
  
  @Override
  public List<CustomTicketModel> populateActivities(String ticket) {
    List<CustomTicketModel> activityModels = new ArrayList<>();
    List<RefLogEntry> rawActivities = getRawActivities();
    CustomTicketModel customTicketModel = new CustomTicketModel();
    int cacheHit = 0;
    int cacheMiss = 0;
    long start = System.currentTimeMillis();

    if (CollectionUtils.isNotEmpty(rawActivities)) {
      for (RefLogEntry refLogEntry : rawActivities) {
        if (activityCache.hasCurrent(getUniqueKeyForActivity(refLogEntry), refLogEntry.date)) {
          ++cacheHit;
          customTicketModel = activityCache.getObject(getUniqueKeyForActivity(refLogEntry));
        } else {
          ++cacheMiss;
          customTicketModel = createActivity(refLogEntry);

          activityCache.updateObject(getUniqueKeyForActivity(refLogEntry), refLogEntry.date,
              customTicketModel);
        }
        activityModels.add(customTicketModel);
      }
    }
    System.out.println("Total time taken to populate activities: "
        + ((System.currentTimeMillis() - start) / 1000.0) + " secs");
    System.out.println("Total activities: " + activityModels.size());
    System.out.println("Cache Hits: " + cacheHit + ", Cache Miss: " + cacheMiss);
    return activityModels;
  }

  private String getUniqueKeyForActivity(RefLogEntry refLogEntry) {
    String str = String.valueOf(refLogEntry.date.getTime());
    if (refLogEntry.getAuthorIdent() != null) {
      str += "_" + refLogEntry.getAuthorIdent().getName();
    }
    return str;
  }
  
  public List<RefLogEntry> getRawActivities() {

    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    Calendar c = Calendar.getInstance();

    Date minimumDate = c.getTime();// set this to last activity added date
    TimeZone timezone = c.getTimeZone();

    // create daily commit digest feed
    List<RefLogEntry> digests = new ArrayList<RefLogEntry>();
    for (RepositoryModel model : repositories) {
      if (model.isCollectingGarbage()) {
        continue;
      }
      if (model.isHasCommits() && model.getLastChange().after(minimumDate)) {
        Repository repository =
            repositoryService.getRepositoryManager().getRepository(model.getName());

        if (repository != null) {
          List<RefLogEntry> entries =
             (List<RefLogEntry>) // RefLogUtils.getDailyLogByRef(model.getName(), repository, minimumDate, timezone);
          RefLogUtils.getRefLogBranch(repository);
          updateLastActivityInsertedPerRepo(model, entries);

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

    for (RepositoryModel repo : lastActivityPerRepo.keySet()) {
      System.out.println("LastActivity for " + repo.getName() + ": "
          + lastActivityPerRepo.get(repo));
    }
    return digests;
  }
  
  private void updateLastActivityInsertedPerRepo(RepositoryModel model, List<RefLogEntry> entries) {
    Date lastActivityDate = new Date(0);
    if (CollectionUtils.isNotEmpty(entries)) {
      lastActivityDate = entries.get(0).date;
    }
    lastActivityPerRepo.put(model, lastActivityDate);
  }
  private CustomTicketModel createActivity(RefLogEntry change) {
    CustomTicketModel customTicketModel = new CustomTicketModel();

    

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
    
    customTicketModel.setTicketId(ticketId);
    customTicketModel.setByAuthor(by);
    customTicketModel.setRepositoryName(repoName);
      
    return customTicketModel;
  }
}