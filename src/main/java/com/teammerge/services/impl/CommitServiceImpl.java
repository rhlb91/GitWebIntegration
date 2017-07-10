package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.CommitModel;
import com.teammerge.model.ExtCommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

@Service("commitService")
public class CommitServiceImpl implements CommitService {
  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  public List<ExtCommitModel> getDetailsForBranchName(String branchName) {
    List<ExtCommitModel> commits = new ArrayList<ExtCommitModel>();
    int numOfMatchedBranches = 0;

    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    Calendar c = Calendar.getInstance();
    c.setTime(new Date(0));
    Date minimumDate = c.getTime();

    for (RepositoryModel model : repositories) {
      if (model.isCollectingGarbage()) {
        continue;
      }

      Repository repository =
          repositoryService.getRepository(model.getName());
      List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

      if (CollectionUtils.isNotEmpty(branchModels)) {
        for (RefModel branch : branchModels) {
          if (branch.getName().contains(branchName)) {
            ++numOfMatchedBranches;

            if (repository != null && model.isHasCommits()) {
              List<RepositoryCommit> repoCommitsPerBranch = CommitCache.instance()
                  .getCommits(model.getName(), repository, branch.getName(), minimumDate);

              commits.addAll(populateCommits(repoCommitsPerBranch, model.getName(), branch));
            }
          }
        }
      }
    }
    LOG.debug("Num of branches: " + numOfMatchedBranches + ", Num of commits: " +commits.size() );

    return commits;
  }

  public List<ExtCommitModel> populateCommits(List<RepositoryCommit> commits, String repoName,
      RefModel branch) {
    List<ExtCommitModel> populatedCommits = new ArrayList<ExtCommitModel>();
    for (RepositoryCommit commit : commits) {
      ExtCommitModel commitModel = new ExtCommitModel();
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

      commitModel.setBranchName(branch.displayName);
      commitModel.setRepositoryName(repoName);
      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }
}
