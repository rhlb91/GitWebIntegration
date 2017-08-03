package com.teammerge.services.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.dao.ScheduleDao;
import com.teammerge.dao.impl.BranchDao;
import com.teammerge.entity.Company;
import com.teammerge.model.BranchModel;
import com.teammerge.model.ScheduleJobModel;
import com.teammerge.services.ScheduleService;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService{
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	  private BaseDao<ScheduleJobModel> baseDao;
	

	@Override
	public ScheduleJobModel getSchedule(String jobId) {
		ScheduleJobModel scheduleJobModel = baseDao.fetchEntity(jobId);
		return scheduleJobModel;
	}
	
	@Autowired
	  public void setBaseDao(BaseDao<ScheduleJobModel> baseDao) {
	    baseDao.setClazz(ScheduleJobModel.class);
	    this.baseDao = baseDao;
	  }
		
	  public int saveSchedule(ScheduleJobModel scheduleJobModel) {
		  baseDao.saveEntity(scheduleJobModel);
	    return 0;
	  }
}