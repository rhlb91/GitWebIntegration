package com.teammerge.dao;

import com.teammerge.entity.Company;

public interface CompanyDao extends BaseDao<Company> {

  /**
   * returns the remote url for the specified project Id.
   * 
   * <br>
   * <br>
   * Since we are not passing companyId in this function, thus the first non empty remoteUrl will be
   * returned
   * 
   * @param projectId projectId/projectName
   * @return remoteUrl
   */
  String getRemoteUrlForProject(final String projectId);

}
