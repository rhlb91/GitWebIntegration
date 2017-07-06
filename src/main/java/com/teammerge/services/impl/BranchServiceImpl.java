package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.CharMatcher;
import com.teammerge.Constants;
import com.teammerge.model.BranchModel;
import com.teammerge.model.ExtCommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

@Service("branchService")
public class BranchServiceImpl implements BranchService {
  
  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;
  
 
  @Override
  public List<BranchModel> getBranchName(String branchName) {
   
    // TODO Auto-generated method stub
   BranchModel brh=new BranchModel();
    List<BranchModel> branchs = new ArrayList<>();
    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();
    for (RepositoryModel model : repositories) {
      if (model.isCollectingGarbage()) {
        continue;
      }
    
    Repository repository =
        repositoryService.getRepositoryManager().getRepository(model.getName());
    List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);
    if (CollectionUtils.isNotEmpty(branchModels)) {
    for (RefModel branch : branchModels) {
      if (branch.getName().contains(branchName)) {
        if (repository != null && model.isHasCommits()) {
          List<RepositoryCommit> repoCommitsPerBranch = CommitCache.instance()
              .getCommits(model.getName(), repository, branch.getName());
          branchs.addAll(populateBranchs(repoCommitsPerBranch, branch,branchName));
        }
    }
    
    // brh.setBranchName(branch.getName());
    
    
  }
    }
  }
    return branchs;
  }


public List<BranchModel> populateBranchs(List<RepositoryCommit> repoCommitsPerBranch,RefModel branch,String name) {
  List<BranchModel> populateBranchs = new ArrayList<>();
    BranchModel branchModel = new BranchModel();
    branchModel.setBranchName(branch.getName());
    
    //int hashLen = 6;
    if (branch.getName() != null) {
      //branchModel.setShortName(branch.getName().substring(0, hashLen));
//      String as=CharMatcher.is('\\').trimFrom("joe\\jill");
      branchModel.setShortName(name);
    }
  populateBranchs.add(branchModel);
  
  return populateBranchs;
}
}