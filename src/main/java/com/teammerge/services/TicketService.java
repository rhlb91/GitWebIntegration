package com.teammerge.services;

import java.util.ArrayList;

import javax.ws.rs.PathParam;

import com.teammerge.model.Ticketmodel;

public interface TicketService {
    public ArrayList<Ticketmodel> GetTicket(@PathParam("param") String message)throws Exception;
    
    public ArrayList<Ticketmodel> GetCommit(@PathParam("param") String message)throws Exception;

}
