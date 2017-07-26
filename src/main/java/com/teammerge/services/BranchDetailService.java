package com.teammerge.services;

import java.util.List;

import com.teammerge.model.BranchDetailModel;

public interface BranchDetailService {

  BranchDetailModel getBranchDetails(String branchId);

  int saveBranch(BranchDetailModel branch);

  List<BranchDetailModel> getBranchDetailsForBranchLike(String branchId);
}
