package com.teammerge.services;

import java.util.List;

import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RepositoryCommit;

public interface DashBoardService {
	public List<DailyLogEntry> getRawActivities();

	public List<ActivityModel> populateActivities();

	public List<CommitModel> populateCommits(List<RepositoryCommit> commits,
			DailyLogEntry change);
}
