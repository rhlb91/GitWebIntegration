package com.teammerge.services.impl;

import java.sql.Connection;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ws.rs.PathParam;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.teammerge.dao.Database;
import com.teammerge.dao.TicketDao;
import com.teammerge.dao.impl.TicketDaoImpl;
import com.teammerge.model.Ticketmodel;
import com.teammerge.services.TicketService;


public class TicketServiceImpl implements TicketService {
  

  public ArrayList<Ticketmodel> GetTicket(@PathParam("param") String ticketId)throws Exception {
    ApplicationContext context =
        new ClassPathXmlApplicationContext("applicationContext.xml");

    TicketDao ticketDao = (TicketDao) context.getBean("ticketDAO");  
        ArrayList<Ticketmodel> ticket=ticketDao.GetTicketDao(ticketId);
    return ticket;
        }
        

public ArrayList<Ticketmodel> GetCommit(@PathParam("param") String message)throws Exception {
  ApplicationContext context =
      new ClassPathXmlApplicationContext("applicationContext.xml");
  
  TicketDao ticketDao = (TicketDao) context.getBean("ticketDAO");
  ArrayList<Ticketmodel> ticket=ticketDao.GetCommitDao(message);
  
  return ticket;
  }

  }
