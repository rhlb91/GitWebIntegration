package com.teammerge.dao;

import java.util.List;

import com.teammerge.entity.Company;
import com.teammerge.entity.CompanyKey;
import com.teammerge.entity.RepoCredentials;

public interface CompanyDao extends BaseDao<Company> {

  Company fetchEntity(CompanyKey key);

  Company fetchEntity(String cName, String pName);

  List<Company> fetchEntityForName(String cName);

}
