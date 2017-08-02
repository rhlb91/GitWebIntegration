package com.teammerge.rest.v3;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.teammerge.cronjob.MannualDataInsertionCronJob;
import com.teammerge.rest.AbstractController;

@Component
@Path("/v3")
public class JobRestController extends AbstractController {// class

  @Resource(name = "mannualDataInsertionCronJob")
  private MannualDataInsertionCronJob cronJob;

  @GET
  @Path("/")
  public Response hello() {
    return Response.status(200).entity("Hi Rest Cron Job working fine!!").build();
  }

  @GET
  @Path("/updateAllBranchs")
  public Response getCronJobForBranch() {
    // CronJob obj = new CronJob();
    cronJob.runJobSavingForBranchDetails();
    String output = "Get Branch  Details has been run sucessfully";
    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/updateAllCommits")
  public Response getCronJobForCommit() {
    // CronJob obj = new CronJob();
    cronJob.runJobSavingForCommitDetails();
    String output = "Get Commit Details has been run sucessfully";
    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

}
