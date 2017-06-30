package com.teammerge.rest.v1;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.teammerge.manager.IRepositoryManager;
import com.teammerge.model.ActivityModel;
import com.teammerge.model.RefModel;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.JacksonUtils;

@Component
@Path("/v1")
public class RestController {

  @Resource(name = "repositoryService")
  private RepositoryService repositoryService;

  @Resource(name = "dashBoardService")
  private DashBoardService dashBoardService;


  private IRepositoryManager getRepositoryManager() {
    return repositoryService.getRepositoryManager();
  }

  @GET
  @Path("/")
  public Response hello() {
    return Response.status(200).entity("hi rfsdal").build();
  }

  @GET
  @Path("/hello/{param}")
  public Response getMsg(@PathParam("param") String msg) {
    String output = "Jersey say : " + msg;
    return Response.status(200).entity(output).build();
  }

  @GET
  @Path("/dataTable")
  public Response sampleDataTableExample() {
    String output = "";

    output += "{";


    output += "\"data\": [";
    output += "{";
    output += "\"name\": \"Tiger Nixon\",";
    output += "\"position\": \"System Architect\",";
    output += "\"salary\": \"$320,800\",";
    output += "\"start_date\": \"2011/04/25\",";
    output += "\"office\": \"Edinburgh\",";
    output += "\"extn\": \"5421\"";
    output += "}]}";
    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/repositories")
  public Response getRepositoriesName() {
    List<String> list = getRepositoryManager().getRepositoryList();
    System.out.println("\n\n " + list.size() + "\n\n");
    String output = "";

    for (String list1 : list) {
      System.out.println("\n\n " + list1 + "\n\n");
      output += list1 + "<br>";
    }

    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/repository/gitlist")
  public Response getRepoForBrowser() {
    Repository repo = getRepository("gitlist");
    String output = null;
    if (repo == null) {
      output = "Error in loading repository 'Gitlist'";
      return Response.status(200).entity(output).build();
    }
    output = "Repository 'gitlist' successfully loaded!!";
    System.out.println("Repo: " + repo);
    return Response.status(200).entity(output).build();
  }

  @GET
  @Path("/{repository}/commit")
  public Response getCommit(@PathParam("repository") String repoName) {
    String output = null;
    Repository repo = getRepository(repoName);

    RevCommit commit = JGitUtils.getCommit(repo, null);

    if (commit != null) {
      Date commitDate = JGitUtils.getCommitDate(commit);
      System.out.println("Commit: " + commit);
      System.out.println("Message: " + commit.getFullMessage());
      System.out.println("Name:" + commit.getName());
      output = "Commit Date: " + commitDate;
    } else {
      output = "Unable to find commit!!";
    }
    return Response.status(200).entity(output).build();
  }

  @GET
  @Path("/{repository}/commit/{branch}")
  public Response getAllCommits(@PathParam("repository") String repoName,
      @PathParam("branch") String branch) {
    Repository repo = getRepository(repoName);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -20);
    System.out.println("Date = " + cal.getTime());

    List<RevCommit> commits = JGitUtils.getRevLog(repo, branch, cal.getTime());
    String output = "";
    if (CollectionUtils.isNotEmpty(commits)) {
      for (RevCommit commit : commits) {
        output += commit.getFullMessage() + "<br>";
      }
    } else {
      output = "No commits found for the branch: " + branch;
    }
    return Response.status(200).entity(output).build();
  }

  @GET
  @Path("/{repository}/branches")
  public Response getAllBranches(@PathParam("repository") String repoName) {
    ObjectId object = null;
    String output = "";

    Repository repo = getRepository(repoName);
    List<RefModel> branchModels = JGitUtils.getRemoteBranches(repo, true, -1);
    output += "Branches found: " + branchModels.size() + "<br><br>";

    if (branchModels.size() > 0) {

      for (RefModel branch : branchModels) {
        object = branch.getReferencedObjectId();
        output +=
            branch.getName() + "--" + "Referenced Object Id: " + object + ", Object Id:"
                + branch.getObjectId() + "<br>";
      }
    }

    return Response.status(200).entity(output).build();

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
    String jsonOutput = JacksonUtils.toJson(activities);
    String finalOutput = "{ \"data\":" + jsonOutput + "}";
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  protected Repository getRepository(String repositoryName) {
    Repository r = getRepositoryManager().getRepository(repositoryName);
    if (r == null) {
      System.out.println("\n\nCannot Load Repository" + " " + repositoryName);
      return null;
    }
    return r;
  }

}
