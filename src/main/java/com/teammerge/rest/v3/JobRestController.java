package com.teammerge.rest.v3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.teammerge.utils.StringUtils;
import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import com.teammerge.cronjob.MannualDataInsertionCronJob;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.rest.AbstractController;

@Component
@Path("/v3")
public class JobRestController extends AbstractController {
  @Resource(name = "mannualDataInsertionCronJob")
  private MannualDataInsertionCronJob cronJob;

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
    if ((StringUtils.isEmpty(repoName)) || (StringUtils.isEmpty(branchId))) {
      String output = "Project id and Ticket id should not be empty";
      result.put("result", output);
    } else {
      cronJob.fetchAndSaveBranchAndCommitDetailsOnline(repoName, branchId);
      String output = "Branch and Commit Details has been updated sucessfully";
      result.put("result", output);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }
}
