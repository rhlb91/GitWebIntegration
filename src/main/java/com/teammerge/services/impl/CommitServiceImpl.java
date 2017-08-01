package com.teammerge.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.dao.BaseDao;
import com.teammerge.dao.CommitDetailDao;
import com.teammerge.form.CommitForm;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.strategy.CommitDiffStrategy;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

@Service("commitService")
public class CommitServiceImpl implements CommitService {
  private static final Logger LOG = LoggerFactory.getLogger(CommitServiceImpl.class);

  private BaseDao<CommitModel> baseDao;
  
  private CommitDetailDao commitDao;
    
  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  @Resource(name = "commitDiffStrategy")
  private CommitDiffStrategy commitDiffStrategy;

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  /**
   * Here a branch represents a ticket, commits inside a branch represents the work done for that
   * ticket <br>
   * 
   * Thus branch name should be same as ticket id
   */
  public Map<String, List<CommitModel>> getDetailsForBranchName(String branchName) {

    Map<String, List<CommitModel>> commitsPerMatchedBranch = new HashMap<>();
    List<RepositoryCommit> repoCommits = new ArrayList<>();
    List<CommitModel> commits = new ArrayList<CommitModel>();
    int numOfMatchedBranches = 0;
    Date minimumDate = TimeUtils.getInceptionDate();

    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    if (CollectionUtils.isEmpty(repositories) || StringUtils.isEmpty(branchName)) {
      return commitsPerMatchedBranch;
    }

    for (RepositoryModel repoModel : repositories) {
      if (repoModel.isCollectingGarbage()) {
        continue;
      }

      Repository repository = repositoryService.getRepository(repoModel.getName());
      List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

      if (CollectionUtils.isNotEmpty(branchModels)) {
        for (RefModel branch : branchModels) {
          repoCommits.clear();
          if (branch.getName().contains(branchName)) {
            ++numOfMatchedBranches;


            if (repository != null && repoModel.isHasCommits()) {
              List<RepositoryCommit> commitsPerBranch =
                  CommitCache.instance().getCommits(repoModel.getName(), repository,
                      branch.getName(), minimumDate);

              commits.addAll(populateCommits(commitsPerBranch, repoModel.getName(), branch));
            }
            commitsPerMatchedBranch.put(branch.displayName, commits);
          }
        }
      }
    }

    if (isDebugOn()) {
      LOG.debug("Num of branches: " + numOfMatchedBranches + ", Num of commits: " + commits.size());
    }

    return commitsPerMatchedBranch;
  }

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits, String repoName,
      RefModel branch) {
    List<CommitModel> populatedCommits = new ArrayList<CommitModel>();

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
      commitModel.setCommitId(commit.getName());
      if (commitModel.getShortMessage().startsWith("Merge")) {
        commitModel.setIsMergeCommit(true);
      } else {
        commitModel.setIsMergeCommit(false);
      }

      commitModel.setCommitDate(commit.getCommitDate());
      commitModel.setCommitTimeFormatted(TimeUtils.convertToDateFormat(commit.getCommitDate(),
          commitTimeFormat));
      commitModel.setBranchName(branch.displayName);
      commitModel.setRepositoryName(repoName);

      setParent(commitModel, commit.getParents());
      commitModel.setParentCount(commit.getParentCount());

      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }

  private void setParent(CommitModel commitModel, RevCommit[] parentCommits) {
    List<String> parents = new ArrayList<>();

    if (parents != null) {
      for (RevCommit commit : parentCommits) {
        parents.add(commit.getName());
      }
      commitModel.setParents(parents);
    }
  }

  @Override
  public List<CommitModel> getCommitDetails(String branchName) {
    List<CommitModel> commitModel = commitDao.fetchEntityLike(branchName);
    return commitModel;
  }

  public void saveCommit(CommitModel commit) {
    getBaseDao().saveEntity(commit);

  }

  public BaseDao<CommitModel> getBaseDao() {
    return baseDao;
  }

  @Autowired
  public void setBaseDao(BaseDao<CommitModel> baseDao) {
    baseDao.setClazz(CommitModel.class);
    this.baseDao = baseDao;
  }

  /**
   * This method is used to save/update commitdetails in Dao, Using CommitForm to populate the data
   * for binding in Json format at the times parsing data from CommitModel to databases
   */
  @Override
  public void saveOrUpdateCommitDetails(CommitForm commitForm) {
    // TODO Auto-generated method stub
    CommitModel model = new CommitModel();
    // getBaseDao().fetchEntity(commitForm);

    model.setCommitId(commitForm.getCommitId());
    model.setCommitAuthor(new PersonIdent(commitForm.getAuthorName(), commitForm.getAuthorEmail(),
        Long.valueOf(commitForm.getWhen()), Integer.valueOf(commitForm.getTimezone())));
    model.setBranchName(commitForm.getBranchName());
    model.setCommitDate(TimeUtils.convertToDateFormat(Long.valueOf(commitForm.getCommitDate())));
    model.setCommitHash(commitForm.getCommitHash());
    model.setCommitTimeFormatted(commitForm.getFormattedTime());
    model.setIsMergeCommit(Boolean.valueOf(commitForm.getIsMergeCommit()));
    model.setRepositoryName(commitForm.getRepoName());
    model.setShortMessage(commitForm.getShortMsg());
    model.setTrimmedMessage(commitForm.getTrimmedMsg());

    getBaseDao().saveEntity(model);

  }

  public List<String> getCommitDiff(String repoName, String branch, String commitId)
      throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException,
      IOException {
    Repository r = repositoryService.getRepository(repoName);
    return commitDiffStrategy.getCommitDif(r, null, commitId);
  }

  @Override
  public List<CommitModel> getCommitDetailsAll() {
    return getBaseDao().fetchAll();
  }
  
  @Autowired
  public void setCommitDao(CommitDetailDao commitDao) {
    commitDao.setClazz(CommitModel.class);
    this.commitDao = commitDao;
  }

}
