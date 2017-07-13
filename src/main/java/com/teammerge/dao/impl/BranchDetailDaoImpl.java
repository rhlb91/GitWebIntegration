package com.teammerge.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.BranchDetailDao;
import com.teammerge.dao.TicketDao;
import com.teammerge.rest.model.BranchDetailModel;
import com.teammerge.rest.model.Ticketmodel;

@Repository("branchDetailDao")
public class BranchDetailDaoImpl implements BranchDetailDao {

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
   this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public BranchDetailModel getBranchDetails(String branchId) {
    BranchDetailModel branchDetails = null;
    try {
      branchDetails = jdbcTemplate.queryForObject("SELECT * FROM branchDetails WHERE branchID = ?",
       new Object[] { branchId }, new BeanPropertyRowMapper<BranchDetailModel>(BranchDetailModel.class));
    System.out.println("branchDetails ::"+branchDetails);
    } catch (DataAccessException e) {
     e.printStackTrace();
    }
    return branchDetails;

   }

  @Override
  public int deleteBranchdao(String branchId) {
    // TODO Auto-generated method stub
    int count = jdbcTemplate.update("DELETE from branchDetails WHERE branchID = ?", new Object[] { branchId });
    return count;
  }

  @Override
  public int updateBranchdao(BranchDetailModel branch) {
    int count = jdbcTemplate.update(
        "UPDATE branchDetails set num_of_commits = ? , num_of_pull = ? , num_of_branches= ?, last_Modified_Date = ? ,repositaryID =? where branchID = ?", new Object[] {
            branch.getNumOfCommits(), branch.getNumOfPull(),branch.getNumOfBranches(), branch.getLastModifiedDate().toString(), branch.getRepositaryId(),branch.getBranchId() });
      return count;
  }

  @Override
  public int createBranchdao(BranchDetailModel branch) {
    // TODO Auto-generated method stub
    int count = jdbcTemplate.update(
        "INSERT INTO branchDetails(branchID,num_of_commits, num_of_pull, num_of_branches,last_Modified_Date,repositaryID)VALUES(?,?,?,?,?,?)", new Object[] {
            branch.getBranchId(),branch.getNumOfCommits(), branch.getNumOfPull(),branch.getNumOfBranches(), branch.getLastModifiedDate().toString(), branch.getRepositaryId() });
      return count;
  }

  

}