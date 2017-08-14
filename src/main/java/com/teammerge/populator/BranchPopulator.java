package com.teammerge.populator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.GitWebException;
import com.teammerge.entity.BranchModel;
import com.teammerge.model.CustomRefModel;
import com.teammerge.utils.TimeUtils;

@Component
public class BranchPopulator {

  @Value("${git.branch.dateFormat}")
  private String branchDateFormat;


  public void populate(CustomRefModel source1, int numOfCommits, BranchModel target)
      throws GitWebException.InvalidArgumentsException {

    if (source1 == null || target == null) {
      throw new GitWebException.InvalidArgumentsException("One of the parameters null");
    }

    target.setBranchId(source1.getRefModel().getName());
    target.setNumOfCommits(numOfCommits);
    target.setRepositoryId(source1.getRepositoryName());

    target.setLastModifiedDate(TimeUtils.convertToDateFormat(source1.getRefModel().getDate(),
        branchDateFormat));

    target.setShortName(source1.getRefModel().displayName);

    // Not used as of now, but can be used in future
    target.setNumOfPull(0);

  }
}
