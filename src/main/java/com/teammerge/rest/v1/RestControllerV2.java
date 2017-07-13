package com.teammerge.rest.v1;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.rest.model.BranchDetailModel;
import com.teammerge.rest.model.CommitDetailModel;
import com.teammerge.rest.model.Ticketmodel;
import com.teammerge.services.BranchDetailService;
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
  
  @Resource(name = "branchDetailService")
  private BranchDetailService branchDetailService;

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
  
  @GET
  @Path("/branchDetails/{id}")
  
  public Response getBranchDetails(@PathParam("id") String branchId) {
    BranchDetailModel branchDetailModel = branchDetailService.getBranchDetailService(branchId);
    String jsonOutput = JacksonUtils.toBranchDetailJson(branchDetailModel); 
    String finalOutput = convertToFinalOutput(jsonOutput); 
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*") 
        .build(); 
    }   
  
  @GET
  @Path("/branchDetails/delete/{id}")
  public Response getBranchDelete(@PathParam("id") String branchId) {
  
      String finalOutput = "";

      BranchDetailModel branchDetailModel = branchDetailService.getBranchDetailService(branchId);

      int Count = 0;
      if(branchId!=null)
      {
        branchDetailService.deleteBranchDetails(branchId);
     Count++;   
      }
     
      finalOutput =
          convertToFinalOutput("{\"numOfbranches deleted\": " + Count + "}");

      return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
          .build();
    }
  
  
  @PUT
  @Path("/branchDetails/update/{id}")
  @Produces("application/json")
  public Response getUpdategetBranchDetails(@PathParam("id") String branchId,BranchDetailModel branch) {
  
      String finalOutput = "";

      int Count = 0;
      if(branchId!=null)
      {
        branchDetailService.upadateBranchDetails(branch);
     Count++;   
      }
     
      finalOutput =
          convertToFinalOutput("{\"numOfbranches updated\": " + Count + "}");

      return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
          .build();
    }

  @POST
  @Path("/branches")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createBranchDetails(BranchDetailModel branchs) {
    int count = 0;
    branchDetailService.createBranch(branchs);
    count++;
   String finalOutput =
        convertToFinalOutput("{\"branch created successfully\": " + count + "}");

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
   }
  
  private String convertToFinalOutput(final String output) {
    return "{ \"data\":" + output + "}";
  }
}
