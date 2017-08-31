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
    return createResponse(200, result);
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
    return createResponse(200, result);
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
    return createResponse(200, result);
  }

  @GET
  @Path("/updateCurrentBranch/{repoId}/{id}")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response updateCurrentBranch(@PathParam("repoId") String repoName,
      @PathParam("id") String branchId) {
    Map<String, Object> result = new HashMap<>();
    if ((StringUtils.isEmpty(repoName)) || (StringUtils.isEmpty(branchId))) {
      result.put("result", "error");
      result.put("reason", "Project id and Ticket id should not be empty");
    } else {
      cronJob.fetchAndSaveBranchAndCommitDetailsOnline(repoName, branchId);
      result.put("result", "success");
      result.put("output", "Branch and Commit Details has been updated sucessfully");
    }
    return createResponse(200, result);
  }

  @GET
  @Path("/reschedule/{jobName}/{cronExpression}")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response reschedule(@PathParam("jobName") String jobName,
      @PathParam("cronExpression") String cronExpression) {
    Map<String, Object> result = new HashMap<>();
    String dataJobName = DataInsertionJob.class.getSimpleName();
    if ((StringUtils.isEmpty(jobName)) || (StringUtils.isEmpty(cronExpression))) {
      result.put("result", "error");
      result.put("reason", "Job Name and CronExpression should not be empty");
    } else if ((!dataJobName.equalsIgnoreCase(jobName))) {
      result.put("result", "error");
      result.put("reason", "Job Name is not matching with Existing job");
    } else {
      schedulerService.reschedule(DataInsertionJob.class, cronExpression);
      result.put("result", "success");
      result.put("output", jobName + " has been rescheduled sucessfully");
    }
    return createResponse(200, result);
  }

  @GET
  @Path("/pauseJob/{jobName}")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response pauseJob(@PathParam("jobName") String jobName) {
    Map<String, Object> result = new HashMap<>();
    if (StringUtils.isEmpty(jobName)) {
      result.put("result", "error");
      result.put("reason", "Job id should not be empty");
    } else {
      schedulerService.pauseJob(jobName);
      result.put("result", "success");
      result.put("output", jobName + " has been paused sucessfully");
    }
    return createResponse(200, result);
  }

  @GET
  @Path("/resumeJob/{jobName}/{jobGroup}")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response resumeJob(@PathParam("jobName") String jobName,
      @PathParam("jobGroup") String jobGroup) {
    Map<String, Object> result = new HashMap<>();
    if ((StringUtils.isEmpty(jobName)) || (StringUtils.isEmpty(jobGroup))) {
      result.put("result", "error");
      result.put("reason", "Job Name and job Group should not be empty");
    } else {
      schedulerService.resumeJob(jobName, jobGroup);
      result.put("result", "success");
      result.put("output", jobName + " has been resumed sucessfully");
    }
    return createResponse(200, result);
  }

}
