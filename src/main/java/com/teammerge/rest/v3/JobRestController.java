package com.teammerge.rest.v3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.teammerge.cronjob.DataInsertionJob;
import com.teammerge.cronjob.MannualDataInsertionCronJob;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.rest.AbstractController;
import com.teammerge.services.SchedulerService;
import com.teammerge.utils.StringUtils;

@Component
@Path("/v3")
public class JobRestController extends AbstractController {
  @Resource(name = "mannualDataInsertionCronJob")
  private MannualDataInsertionCronJob cronJob;

  @Resource(name = "schedulerService")
  private SchedulerService schedulerService;

  @GET
  @Path("/")
  public Response hello() {
    return Response.status(200).entity("Hi Rest Cron Job working fine!!").build();
  }

  @GET
  @Path("/updateAllBranchs")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response runCronJobForBranch() {

    Map<String, Object> result = new HashMap<>();
    List<RefModel> failedBranches = cronJob.runJobSavingForBranchDetails();

    String output = "Branch details has been saved sucessfully";
    if (CollectionUtils.isNotEmpty(failedBranches)) {
      output += "However, there are some failed entries!! Try to add them mannually!!";
    }
    result.put("result", output);
    result.put("failedEntries", failedBranches);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/updateAllCommits")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response runCronJobForCommit() {
    Map<String, Object> result = new HashMap<>();

    List<RepositoryCommit> failedCommits = cronJob.runJobSavingForCommitDetails();
    String output = "Commit Details has been saved sucessfully";

    if (CollectionUtils.isNotEmpty(failedCommits)) {
      output += "However, there are some failed entries!! Try to add them mannually!!";
    }
    result.put("result", output);
    result.put("failedEntries", failedCommits);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/updateAllDetails")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response runSaveAllDetails() {
    Map<String, Object> result = new HashMap<>();

    List<Object> failedEntries = cronJob.runSaveAllDetails();
    String output = "Branches and Commit Details has been saved sucessfully";

    if (CollectionUtils.isNotEmpty(failedEntries)) {
      output += "However, there are some failed entries!! Try to add them mannually!!";
    }
    result.put("result", output);
    result.put("failedEntries", failedEntries);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @Path("/updateCurrentBranch/{repoId}/{id}")
  public Response updateCurrentBranch(@PathParam("repoId") String repoName,
      @PathParam("id") String branchId) {
    Map<String, Object> result = new HashMap<>();
    String output = null;
    if ((StringUtils.isEmpty(repoName)) || (StringUtils.isEmpty(branchId))) {
      output = "Project id and Ticket id should not be empty";
    } else {
      cronJob.fetchAndSaveBranchAndCommitDetailsOnline(repoName, branchId);
      output = "Branch and Commit Details has been updated sucessfully";
    }
    result.put("result", output);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @Path("/reschedule/{jobName}/{cronExpression}")
  public Response reschedule(@PathParam("jobName") String jobName,
      @PathParam("cronExpression") String cronExpression) {
    Map<String, Object> result = new HashMap<>();
    String output = null;
    String dataJobName = DataInsertionJob.class.getSimpleName();
    if (((StringUtils.isEmpty(jobName)) || (StringUtils.isEmpty(cronExpression)))
        && (!dataJobName.equalsIgnoreCase(jobName))) {
      output = "Job Name and CronExpression should not be empty";
    } else {
      schedulerService.reschedule(DataInsertionJob.class, cronExpression);
      output = jobName + " has been rescheduled sucessfully";
    }
    result.put("result", output);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @Path("/getpauseJob/{jobName}")
  public Response getpauseJob(@PathParam("jobName") String jobName) {
    Map<String, Object> result = new HashMap<>();
    String output = null;
    if (StringUtils.isEmpty(jobName)) {
      output = "Job id should not be empty";
    } else {
      schedulerService.getpauseJob(jobName);
      output = jobName + "has been pause sucessfully";
    }
    result.put("result", output);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @Path("/getresumeJob/{jobName}/{jobGroup}")
  public Response getresumeJob(@PathParam("jobName") String jobName,
      @PathParam("jobGroup") String jobGroup) {
    Map<String, Object> result = new HashMap<>();
    String output = null;
    if ((StringUtils.isEmpty(jobName)) || (StringUtils.isEmpty(jobGroup))) {
      output = "Job Name and job Group should not be empty";
    } else {
      schedulerService.getresumeJob(jobName, jobGroup);
      output = jobName + "has been resume sucessfully";
    }
    result.put("result", output);
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

}
