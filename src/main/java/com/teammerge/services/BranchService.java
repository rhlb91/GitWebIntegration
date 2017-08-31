package com.teammerge.services;

import java.util.Date;
import java.util.List;

import com.teammerge.entity.BranchModel;
import com.teammerge.form.BranchForm;

public interface BranchService {

  public List<BranchModel> getBranchesWithMinimumDetails(String branchName);

  BranchModel getBranchDetails(String branchId);

  int saveBranch(BranchModel branch);

  List<BranchModel> getBranchDetailsForBranchLike(String branchId);

  Date getLastCommitDateAddedInBranch(String entityKey);

  void updateLastCommitDateAddedInBranch(String entityKey, Date date);

  int saveOrUpdateBranch(BranchModel branch);

  void saveOrUpdateBranchInSeparateSession(BranchModel commit);
  
  void saveBranch(final BranchForm branchForm);

  int removeBranchesForProject(String projectName);

  int removeBranchLastCommitAddedForProjectStartsWith(String projectName);

}
