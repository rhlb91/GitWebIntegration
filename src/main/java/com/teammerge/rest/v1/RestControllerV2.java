package com.teammerge.rest.v1;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.rest.model.Ticketmodel;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.services.impl.TicketServiceImpl;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;

@Component
@Path("/v2")
public class RestControllerV2 {
  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  @Resource(name = "dashBoardService")
  private DashBoardService dashBoardService;

  @Resource(name = "commitService")
  private CommitService commitService;

  @Resource(name = "branchService")
  private BranchService branchService;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @GET
  @Path("/")
  public Response hello() {
    return Response.status(200).entity("Hi Rest Working working fine!!").build();
  }

  @GET
  @Path("/appPath")
  public Response applicationPaths() {
    String dir = ApplicationDirectoryUtils.getProgramDirectory();
    return Response.status(200).entity("Application Dir: " + dir)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/GetTicket/{param}")
  @Produces("application/json")
  public Response getTicketList(@PathParam("param") String message) throws Exception
  {
  TicketServiceImpl ticketService= new TicketServiceImpl();
  ArrayList<Ticketmodel> ticketData = ticketService.GetTicket(message); 
  String jsonOutput = JacksonUtils.toTicketNamesJson(ticketData);
  String finalOutput = convertToFinalOutput(jsonOutput);
  return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
      .build();
  }

  @GET
  @Path("/GetCommit/{param}")
  @Produces("application/json")
  public Response getCommitList(@PathParam("param") String message) throws Exception
  {
  TicketServiceImpl ticketService= new TicketServiceImpl();
  ArrayList<Ticketmodel> ticketData = ticketService.GetCommit(message);
  String jsonOutput = JacksonUtils.toTicketCommitNamesJson(ticketData);
  String finalOutput = convertToFinalOutput(jsonOutput);
  return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
      .build();
  }
  
  private String convertToFinalOutput(final String output) {
    return "{ \"data\":" + output + "}";
  }
}
