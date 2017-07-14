package com.teammerge.rest.v2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.teammerge.model.BranchDetailModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;

@Component
@Path("/v2")
public class RestControllerV2 extends AbstractController {

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
  @Path("/branchDetails/{id}")
  public Response getBranchDetails(@PathParam("id") String branchId) {
    BranchDetailModel branchDetailModel = getBranchDetailService().getBranchDetails(branchId);
    String jsonOutput = JacksonUtils.toBranchDetailJson(branchDetailModel);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/branches")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createBranchDetails(BranchDetailModel branchs) {
    getBranchDetailService().createBranch(branchs);
    String finalOutput = "true";

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }
}
