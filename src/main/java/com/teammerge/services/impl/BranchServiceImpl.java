package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.teammerge.model.BranchModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.BranchService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

@Service("branchService")
public class BranchServiceImpl implements BranchService {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  @Override
  public List<BranchModel> getBranchName(String branchName) {
    List<BranchModel> branches = new ArrayList<>();
    List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

    if (CollectionUtils.isEmpty(repositories) || StringUtils.isEmpty(branchName)) {
      LOG.warn("Either no repositories or branch name is empty!!");
      return branches;
    }

    for (RepositoryModel repoModel : repositories) {
      if (repoModel.isCollectingGarbage()) {
        continue;
      }

      Repository repository = repositoryService.getRepository(repoModel.getName());
      List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

      if (CollectionUtils.isNotEmpty(branchModels)) {
        for (RefModel branch : branchModels) {
          String fullBranchName = branch.getName();
          if (!StringUtils.isEmpty(fullBranchName) && fullBranchName.contains(branchName)) {
            branches.add(populateBranch(fullBranchName, branchName));
          }
        }
      }
    }
    return branches;
  }

  public BranchModel populateBranch(String fullBranchName, String name) {
    BranchModel branchModel = new BranchModel();
    branchModel.setBranchName(fullBranchName);
    branchModel.setShortName(name);

    return branchModel;
  }
}
