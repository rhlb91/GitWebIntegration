package com.teammerge.services;

import java.util.List;

import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RepositoryCommit;

public interface DashBoardService {

  /**
   * Retrives the activity in a raw format ,i.e, not in {@link ActivityModel} format. <br>
   * Takes a parameter <code>daysBack</code> <br>
   * <br>
   * For Example: <br>
   * if daysBack = 5, it fetches result of last 5 days from today's date <br>
   * If one wants all the result, then specify the value <code>-1</code> <br>
   * 
   * @param daysBack gets the result since specified days back
   * @return
   */
  public List<DailyLogEntry> getRawActivities(final int daysBack);

  /**
   * Populates activity, this method returns all the activities till now that has been made on the
   * configured repositories <br>
   * Takes a parameter named <code>cached</code>, if specified <code>false</code>, all the activity
   * models will be newly created, might take some time (depends on the number of activities) -
   * <i>should be used carefully</i>
   * 
   * @param cached to use cached objects or not, if specified <i>false</i> all the activity models
   *        will be created
   * @param daysBack
   * @return list of activities
   */
  public List<ActivityModel> populateActivities(final boolean cached, final int daysBack);

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits, DailyLogEntry change);
}
