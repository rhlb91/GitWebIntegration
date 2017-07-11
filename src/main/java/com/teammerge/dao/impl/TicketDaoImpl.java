package com.teammerge.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;
import javax.ws.rs.PathParam;

import com.teammerge.dao.TicketDao;
import com.teammerge.rest.model.Ticketmodel;

public class TicketDaoImpl implements TicketDao {
  
  private DataSource dataSource;

  public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
  }
  Connection connection = null;
  public ArrayList<Ticketmodel> GetTicketDao(@PathParam("param") String message) throws Exception {
    ArrayList<Ticketmodel> ticketmodel = new ArrayList<Ticketmodel>();
    String sql="SELECT * FROM ticketdetails where ticketID='" + message + "'";
    try {  
      connection=dataSource.getConnection();
      PreparedStatement ps = connection
          .prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Ticketmodel iticket = new Ticketmodel();
        iticket.setTicketID(rs.getString("ticketID"));
        iticket.setCommitID(rs.getString("commitID"));
        iticket.setNum_branches(rs.getInt("num_pull"));
        iticket.setNum_commit(rs.getInt("num_commit"));
        iticket.setNum_pull(rs.getInt("num_branches"));
        iticket.setCommit_message(rs.getString("commit_message"));
        ticketmodel.add(iticket);
      }
      return ticketmodel;
    } catch (Exception e) {
      throw e;
    }
  }
  
  public ArrayList<Ticketmodel> GetCommitDao(@PathParam("param") String message)throws Exception{
    ArrayList<Ticketmodel> ticketmodel = new ArrayList<Ticketmodel>();
    try {
      connection=dataSource.getConnection();
       PreparedStatement ps = connection
          .prepareStatement("SELECT * FROM ticketdetails where commitID='" + message + "'");
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        Ticketmodel iticket = new Ticketmodel();
        iticket.setTicketID(rs.getString("ticketID"));
        iticket.setCommitID(rs.getString("commitID"));
        iticket.setNum_branches(rs.getInt("num_pull"));
        iticket.setNum_commit(rs.getInt("num_commit"));
        iticket.setNum_pull(rs.getInt("num_branches"));
        iticket.setCommit_message(rs.getString("commit_message"));
        ticketmodel.add(iticket);
      }
      return ticketmodel;
    } catch (Exception e) {
      throw e;
    }
  }
  }