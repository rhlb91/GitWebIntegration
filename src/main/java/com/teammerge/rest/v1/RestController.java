package com.teammerge.rest.v1;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Component;

import com.teammerge.entity.BranchModel;
import com.teammerge.entity.CommitModel;
import com.teammerge.model.ActivityModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.JacksonUtils;
import com.teammerge.utils.TimeUtils;

@Component
@Path("/v1")
public class RestController extends AbstractController {

  @GET
  @Path("/")
  public Response hello() {
    return Response.status(200).entity("Hi Rest Working working fine!!").build();
  }

  @GET
  @Path("/repositories")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getRepositoriesName() {
    List<String> list = getRepositoryService().getRepositoryList();
    String output = "";

    for (String list1 : list) {
      output += list1 + "<br>";
    }
    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/{repository}/commit/{branch}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getAllCommits(@PathParam("repository") String repoName,
      @PathParam("branch") String branch) {
    Repository repo = getRepositoryService().getRepository(repoName, true);
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
  @Produces(MediaType.TEXT_PLAIN)
  public Response getAllBranches(@PathParam("branchName") String branchName) {
    List<BranchModel> branches = getBranchService().getBranchesWithMinimumDetails(branchName);
    String jsonOutput = JacksonUtils.toJson(branches);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/activities")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getActivities() {
    List<ActivityModel> activities = getDashBoardService().populateActivities(true, -1);
    String str = "";
    for (ActivityModel activity : activities) {
      str += activity.toString();
    }
    return Response.status(200).entity(str).build();
  }

  @GET
  @Path("/activitiesInJson")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getActivitiesInJson(@DefaultValue("true") @QueryParam("cached") boolean cached,
      @DefaultValue("-1") @QueryParam("daysBack") int daysBack) {

    List<ActivityModel> activities = getDashBoardService().populateActivities(cached, daysBack);
    String jsonOutput = JacksonUtils.toJson(activities);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/ticket/{ticketid}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getTickets(@PathParam("ticketid") String ticket) {

    List<CommitModel> commits = new ArrayList<>();

    Map<String, List<CommitModel>> commitsPerBranch =
        getCommitService().getDetailsForBranchName(ticket);

    for (String branchStr : commitsPerBranch.keySet()) {
      commits.addAll(commitsPerBranch.get(branchStr));
    }

    String jsonOutput = JacksonUtils.toJson(commits);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/count/{ticketid}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCommitAndBranchCount(@PathParam("ticketid") String ticket) {
    String finalOutput = "";

    Map<String, List<CommitModel>> commitsPerBranch =
        getCommitService().getDetailsForBranchName(ticket);

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

  
}
