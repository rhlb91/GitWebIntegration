package com.teammerge.rest.v2;

import java.io.IOException;
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

import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.CommitDiffRequestForm;
import com.teammerge.form.CommitForm;
import com.teammerge.form.CreateNewBranchForm;
import com.teammerge.form.RepoForm;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.services.GitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;
import com.teammerge.validator.BaseValidator.FieldError;
import com.teammerge.validator.BaseValidator.ValidationResult;
import com.teammerge.validator.impl.CommitDiffValidator;

@Component
@Path("/v2")
public class RestControllerV2 extends AbstractController {

  @Resource(name = "gitService")
  private GitService gitService;

  @Resource(name = "commitDiffValidator")
  private CommitDiffValidator diffValidator;

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
    BranchDetailModel branchDetailModel = getBranchDetailService().getBranchDetails(branchId);
    String jsonOutput = JacksonUtils.toJson(branchDetailModel);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/branch")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveBranchDetails(BranchDetailModel branch) {
    getBranchDetailService().saveBranch(branch);
    String finalOutput = "Saved successfully!!";

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @GET
  @Path("/company/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCompanyDetails(@PathParam("id") String name) {
    Company company = getCompanyDetailService().getCompanyDetails(name);
    String jsonOutput = JacksonUtils.toJson(company);
    String finalOutput = convertToFinalOutput(jsonOutput);
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/company")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCompanyDetails(Company company) {

    getCompanyDetailService().saveCompanyDetails(company);
    String finalOutput = "Saved successfully!!";

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @POST
  @Path("/addCommit")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCommitDetails(CommitForm commit) {
    getCommitService().saveOrUpdateCommitDetails(commit);
    String finalOutput = "Saved successfully!!";
    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
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

    List<BranchDetailModel> branchDetailModel =
        getBranchDetailService().getBranchDetailsForBranchLike(ticketId);

    if (CollectionUtils.isNotEmpty(branchDetailModel)) {
      numOfBranches = branchDetailModel.size();
      for (BranchDetailModel model : branchDetailModel) {
        numOfCommits += model.getNumOfCommits();
      }
    }

    finalOutput =
        convertToFinalOutput("{\"numOfPull\": " + numOfBranches + "," + "\"numOfCommits\": "
            + numOfCommits + "}");

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
  public Response addRepo(RepoForm repoForm) {
    getCompanyDetailService().saveOrUpdateCompanyDetails(repoForm);
    getRepoCredentialService().saveOrUpdateRepoCredentials(repoForm);

    return Response.status(200).entity("Saved successfully!!")
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
    String output = "";

    Map<String, Object> result =
        getRepositoryService().createBranch(form.getCompanyId(), form.getProjectId(),
            form.getBranchName());

    if (RepositoryService.Result.FAILURE.equals(result.get("result"))) {
      output += " { \"result\": " + RepositoryService.Result.FAILURE;
      output += ", \"reason\": " + result.get("reason");
      output += ", \"detailedReason\": " + result.get("completeError");
      output += "}";
    } else {
      output += " { \"result\": " + RepositoryService.Result.SUCCESS;
      output += "}";
    }

    return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/commitDiff")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCommitDiff(final CommitDiffRequestForm form) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = diffValidator.validate(form);

    if (vr.hasErrors()) {

      result.put("result", "Validation error");
      String errors = "";
      for (FieldError e : vr.getErrors()) {
        errors += "[" + e.fieldName + "]-[" + e.fieldError + "]";
      }
      result.put("reason", errors);
      return Response.status(200).entity(result).header("Access-Control-Allow-Origin", "*").build();
    }

    try {
      List<String> diffResult =
          getCommitService().getCommitDiff(form.getRepositoryName(), null,
              form.getCommitId());

      result.put("result", "success");
      result.put("output", diffResult);
    } catch (RevisionSyntaxException | IOException e) {
      result.put("result", "error");
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
    }
    return Response.status(200).entity(result).header("Access-Control-Allow-Origin", "*").build();
  }


}
