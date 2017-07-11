package com.teammerge.rest.v1;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.model.ActivityModel;
import com.teammerge.model.BranchModel;
import com.teammerge.model.ExtCommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.JacksonUtils;

@Component
@Path("/v1")
public class RestController {

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
  @Path("/repositories")
  public Response getRepositoriesName() {
    List<String> list = repositoryService.getRepositoryList();
    String output = "";

    for (String list1 : list) {
      output += list1 + "<br>";
    }
    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/{repository}/commit/{branch}")
  public Response getAllCommits(@PathParam("repository") String repoName,
      @PathParam("branch") String branch) {
    Repository repo = repositoryService.getRepository(repoName, true);
    List<RevCommit> commits = JGitUtils.getRevLog(repo, branch, TimeUtils.getInceptionDate());

    StringBuilder output = new StringBuilder();
    if (CollectionUtils.isNotEmpty(commits)) {
      for (RevCommit commit : commits) {
        output.append(commit.getFullMessage()).append("<br>");
      }
    } else {
      output.append("No commits found for the branch: " + branch);
    }

    return Response.status(200).entity(output.toString()).build();
  }

  @GET
  @Path("/branches/{branchName}")
  public Response getAllBranches(@PathParam("branchName") String branchName) {
    List<BranchModel> branches = branchService.getBranchName(branchName);
    String jsonOutput = JacksonUtils.toBrachNamesJson(branches);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/activities")
  public Response getActivities() {
    List<ActivityModel> activities = dashBoardService.populateActivities(true, -1);
    String str = "";
    for (ActivityModel activity : activities) {
      str += activity.toString();
    }
    return Response.status(200).entity(str).build();
  }

  @GET
  @Path("/activitiesInJson")
  public Response getActivitiesInJson(@DefaultValue("true") @QueryParam("cached") boolean cached,
      @DefaultValue("-1") @QueryParam("daysBack") int daysBack) {

    List<ActivityModel> activities = dashBoardService.populateActivities(cached, daysBack);
    String jsonOutput = JacksonUtils.convertActivitiestoJson(activities);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/ticket/{ticketid}")
  public Response getTickets(@PathParam("ticketid") String ticket) {

    List<ExtCommitModel> commits = new ArrayList<>();

    Map<String, List<ExtCommitModel>> commitsPerBranch =
        commitService.getDetailsForBranchName(ticket);

    for (String branchStr : commitsPerBranch.keySet()) {
      commits.addAll(commitsPerBranch.get(branchStr));
    }

    String jsonOutput = JacksonUtils.toTicketCommitsJson(commits);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/count/{ticketid}")
  public Response getCommitAndBranchCount(@PathParam("ticketid") String ticket) {
    String finalOutput = "";

    Map<String, List<ExtCommitModel>> commitsPerBranch =
        commitService.getDetailsForBranchName(ticket);

    int commitCount = 0;
    for (String branchStr : commitsPerBranch.keySet()) {
      commitCount += commitsPerBranch.get(branchStr).size();
    }
    finalOutput =
        convertToFinalOutput("{\"numOfBranches\": " + commitsPerBranch.keySet().size() + ","
            + "\"numOfCommits\": " + commitCount + "}");

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }
  @GET
  @Path("/appPath")
  public Response applicationPaths() {
    String dir = ApplicationDirectoryUtils.getProgramDirectory();
    return Response.status(200).entity("Application Dir: " + dir)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  private String convertToFinalOutput(final String output) {
    return "{ \"data\":" + output + "}";
  }
}
