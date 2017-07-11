package com.teammerge.dao;

import java.sql.Connection;
import java.util.ArrayList;

import javax.ws.rs.PathParam;

import com.teammerge.rest.model.Ticketmodel;
public interface TicketDao {
    public ArrayList<Ticketmodel> GetTicketDao(@PathParam("param") String message) throws Exception;

    public ArrayList<Ticketmodel> GetCommitDao(@PathParam("param") String message) throws Exception;
}