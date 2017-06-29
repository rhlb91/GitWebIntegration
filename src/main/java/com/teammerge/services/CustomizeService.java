package com.teammerge.services;

import java.util.List;
import java.util.Map;

import com.teammerge.model.ActivityModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomTicketModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;

public interface CustomizeService {

  public List<CustomTicketModel> populateActivities(String ticket);

}
