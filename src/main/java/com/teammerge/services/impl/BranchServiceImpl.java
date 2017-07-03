package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.swing.text.AbstractDocument.BranchElement;

import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.BranchModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomTicketModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RefLogEntry;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TicketModel;
import com.teammerge.services.BranchService;
import com.teammerge.services.CustomizeService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.RefLogUtils;
import com.teammerge.utils.StringUtils;

@Service("branchService")
public class BranchServiceImpl implements BranchService {

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  @Override
  public BranchModel getBranchByName(String repoName) {
    // TODO Auto-generated method stub
    BranchModel ticketModel = new BranchModel();
    List<CommitModel> commits = new ArrayList<>();
   // ObjectId qwe=commits.get(0).getId();
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

      // List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);
      List<RefModel> rev = JGitUtils.getRemoteBranches(repository, true, -1);
      if (rev.size() > 0) {
        for (RefModel branch : rev) {
          if (branch.getName() != null) {
            if (repository != null) {

              List<DailyLogEntry> dailyLogEntries =
                  RefLogUtils.getDailyLogByRef(branch.getName(), repository, minimumDate, timezone);

              for (DailyLogEntry dailyLogEntry : dailyLogEntries) {
                if (dailyLogEntry.getCommitCount() >= 1) {
                  commits.addAll(populateCommits(dailyLogEntry.getCommits(), dailyLogEntry));
               // commits.addAll(get(repoName, repository, null, null));
                }
              }
            }
          }
        }
      }
    }
   // ticketModel.setBranchId(qwe);
    ticketModel.setCommits(commits);

    return ticketModel;
  }

 private List<CommitModel> populateCommits(List<RepositoryCommit> commits,
      DailyLogEntry dailyLogEntry) {
    // TODO Auto-generated method stub
    List<CommitModel> populatedCommits = new ArrayList<>();
    for (RepositoryCommit commit : commits) {
      CommitModel commitModel = new CommitModel();
      commitModel.setCommitAuthor(commit.getAuthorIdent());

      commitModel.setName(commit.getName());
      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }
}

  
 /* protected List<RepositoryCommit> get(String repositoryName, Repository repository, String branch,
      ObjectId sinceCommit) {
    Map<ObjectId, List<RefModel>> allRefs = JGitUtils.getAllRefs(repository, false);
    List<RevCommit> revLog = JGitUtils.getRevLog(repository, sinceCommit.getName(), branch);
    List<RepositoryCommit> commits = new ArrayList<RepositoryCommit>(revLog.size());
    for (RevCommit commit : revLog) {
      RepositoryCommit commitModel = new RepositoryCommit(repositoryName, branch, commit);
      List<RefModel> commitRefs = allRefs.get(commitModel.getId());
      commitModel.setRefs(commitRefs);
      commits.add(commitModel);
    }
    return commits;
  }
}*/