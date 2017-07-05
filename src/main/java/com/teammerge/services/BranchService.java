package com.teammerge.services;

import com.teammerge.model.BranchModel;
import com.teammerge.model.TicketModel;

public interface BranchService {
  
  public BranchModel getBranchByName(String repoName);

}
