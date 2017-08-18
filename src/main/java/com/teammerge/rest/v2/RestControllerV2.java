package com.teammerge.rest.v2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.springframework.stereotype.Component;

import com.teammerge.entity.BranchModel;
import com.teammerge.entity.CommitModel;
import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.CommitDiffRequestForm;
import com.teammerge.form.CommitForm;
import com.teammerge.form.CommitTreeRequestForm;
import com.teammerge.form.CreateNewBranchForm;
import com.teammerge.form.RepoForm;
import com.teammerge.model.PathModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.ScheduleJobModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.services.GitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.strategy.BlobConversionStrategy;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;
import com.teammerge.utils.StringUtils;
import com.teammerge.validator.BaseValidator.FieldError;
import com.teammerge.validator.BaseValidator.ValidationResult;
import com.teammerge.validator.impl.CommitDiffValidator;
import com.teammerge.validator.impl.CommitFormValidator;
import com.teammerge.validator.impl.CommitTreeRequestValidator;
import com.teammerge.validator.impl.CreateNewBranchValidator;
import com.teammerge.validator.impl.RepoFormValidator;

@Component
@Path("/v2")
public class RestControllerV2 extends AbstractController {

  @Resource(name = "gitService")
  private GitService gitService;

  @Resource(name = "commitDiffValidator")
  private CommitDiffValidator diffValidator;

  @Resource(name = "newBranchValidator")
  private CreateNewBranchValidator newBranchValidator;

  @Resource(name = "repoFormValidator")
  private RepoFormValidator repoFormValidator;

  @Resource(name = "commitFormValidator")
  private CommitFormValidator commitFormValidator;

  @Resource(name = "treeValidator")
  private CommitTreeRequestValidator treeValidator;

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
  @Path("/branch/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getBranchDetails(@PathParam("id") String branchId) {
    List<BranchModel> branchDetailModel =
        getBranchService().getBranchDetailsForBranchLike(branchId);
    String jsonOutput = JacksonUtils.toJson(branchDetailModel);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/branch")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveBranchDetails(BranchModel branch) {
    getBranchService().saveBranch(branch);
    String finalOutput = "Saved successfully!!";

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/company/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCompanyDetails(@PathParam("id") String name) {
    Company company = getCompanyService().getCompanyDetails(name);
    String jsonOutput = JacksonUtils.toJson(company);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/company")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCompanyDetails(Company company) {

    getCompanyService().saveCompanyDetails(company);
    String finalOutput = "Saved successfully!!";

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }


  @GET
  @Path("/schedule/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getScheduleDetails(@PathParam("id") String jobId) {

    ScheduleJobModel scheduleJobModel = getSchedulerService().getSchedule(jobId);

    String jsonOutput = JacksonUtils.toJson(scheduleJobModel);

    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/addCommit")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCommitDetails(CommitForm commit) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = commitFormValidator.validate(commit);

    if (vr.hasErrors()) {
      commitFormValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }
    try {

      getCommitService().saveOrUpdateCommitDetails(commit);

      result.put("result", "Saved Successfully");
    } catch (RevisionSyntaxException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/commit/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getTicketCommitDetails(@PathParam("id") String branchName) {
    List<CommitModel> commitModel = getCommitService().getCommitDetails(branchName);
    String jsonOutput = JacksonUtils.toJson(commitModel);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  /**
   * This method is used get all commits Detail list from Dao layer
   * 
   * @return list of commits list in Json format
   */

  @GET
  @Path("/commitDetails")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getAllTicketCommitDetails() {
    List<CommitModel> commitModel = getCommitService().getCommitDetailsAll();
    String jsonOutput = JacksonUtils.toJson(commitModel);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/count/{ticketId}")
  public Response getCommitAndBranchCount(@PathParam("ticketId") String ticketId) {
    String finalOutput = "";
    int numOfBranches = 0;
    int numOfCommits = 0;

    List<BranchModel> branchDetailModel =
        getBranchService().getBranchDetailsForBranchLike(ticketId);

    if (CollectionUtils.isNotEmpty(branchDetailModel)) {
      numOfBranches = branchDetailModel.size();
      for (BranchModel model : branchDetailModel) {
        numOfCommits += model.getNumOfCommits();
      }
    }

    finalOutput = convertToFinalOutput(
        "{\"numOfPull\": " + numOfBranches + "," + "\"numOfCommits\": " + numOfCommits + "}");

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/credentials/{user}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCredentialsDetails(@PathParam("user") String name) {
    RepoCredentials repoCredentials = getRepoCredentialService().getCredentialDetails(name);
    String jsonOutput = JacksonUtils.toJson(repoCredentials);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/addRepo")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({"application/json"})
  public Response addRepo(RepoForm repoForm) {

    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = repoFormValidator.validate(repoForm);

    if (vr.hasErrors()) {
      repoFormValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }
    try {
      getCompanyService().saveOrUpdateCompanyDetails(repoForm);
      getRepoCredentialService().saveOrUpdateRepoCredentials(repoForm);

      result.put("result", "Saved successfully!!");
    } catch (RevisionSyntaxException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/allRepos/")
  public Response getAllRepoModels() {
    List<RepositoryModel> repos = getRepositoryService().getRepositoryModelsFromDB();
    String jsonOutput = JacksonUtils.toJson(repos);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/createBranch")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createBranch(final CreateNewBranchForm form) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = newBranchValidator.validate(form);

    if (vr.hasErrors()) {
      newBranchValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }
    try {
      Map<String, Object> results = null;
      try {
        results = getRepositoryService().createBranch(form.getCompanyId(), form.getProjectId(),
            form.getBranchName(), form.getStartingPoint());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      result.put("result", "success");
      result.put("output", results);
    } catch (RevisionSyntaxException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/commitDiff")
  @Consumes("application/json")
  @Produces({"application/json"})
  public Response getCommitDiff(final CommitDiffRequestForm form) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = diffValidator.validate(form);

    if (vr.hasErrors()) {
      diffValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }

    try {
      List<String> diffResult =
          getCommitService().getCommitDiff(form.getRepositoryName(), null, form.getCommitId());

      result.put("result", "success");
      result.put("output", diffResult);
    } catch (RevisionSyntaxException | IOException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }

    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("{repo}/tree/{commitId}/{path}")
  @Produces({"application/json"})
  public Response getFilesInACommit(@PathParam("repo") String repo,
      @PathParam("commitId") String commitId, @PathParam("path") String path) {
    Map<String, Object> result = new HashMap<>();

    if (path != null)
      path = path.equals("null") ? null : path;

    if (!StringUtils.isEmpty(path)) {
      path = path.replace("!", "/");
    }

    CommitTreeRequestForm form = new CommitTreeRequestForm(repo, commitId, path);

    ValidationResult vr = treeValidator.validate(form);

    if (vr.hasErrors()) {
      treeValidator.putErrorsInMap(result, vr);
      return Response.status(200).entity(result).build();
    }

    try {
      List<PathModel> treeResult = getRepositoryService().getTree2(repo, path, commitId);

      result.put("result", "success");
      result.put("output", treeResult);

    } catch (IOException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }

    return Response.status(200).entity(result).build();
  }

  @GET
  @Path("{repo}/blob/{commitId}/{path}")
  @Produces({"application/json"})
  public Response getSourceCodeOfAFile(@PathParam("repo") String repo,
      @PathParam("commitId") String commitId, @PathParam("path") String path) {
    Map<String, Object> result = new HashMap<>();

    if (path != null)
      path = path.equals("null") ? null : path;

    if (!StringUtils.isEmpty(path)) {
      path = path.replace("!", "/");
    }

    CommitTreeRequestForm form = new CommitTreeRequestForm(repo, commitId, path);

    ValidationResult vr = treeValidator.validate(form);

    if (vr.hasErrors()) {
      treeValidator.putErrorsInMap(result, vr);
      return Response.status(200).entity(result).build();
    }

    try {
      Map<String, Object> blobResult = getRepositoryService().getBlob(repo, path, commitId);

      result.put("result", "success");
      result.put("output", blobResult.get(BlobConversionStrategy.Key.SOURCE.name()));
    } catch (RevisionSyntaxException | IOException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }

    return Response.status(200).entity(result).build();
  }
}
