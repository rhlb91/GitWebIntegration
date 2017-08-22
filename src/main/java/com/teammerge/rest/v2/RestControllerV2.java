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
import org.eclipse.jgit.lib.Ref;
import org.springframework.stereotype.Component;

import com.teammerge.Constants.WebServiceResult;
import com.teammerge.entity.BranchModel;
import com.teammerge.entity.CommitModel;
import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.BranchForm;
import com.teammerge.form.CommitDiffRequestForm;
import com.teammerge.form.CommitForm;
import com.teammerge.form.CommitTreeRequestForm;
import com.teammerge.form.CompanyForm;
import com.teammerge.form.CreateNewBranchForm;
import com.teammerge.form.CredentialRequestForm;
import com.teammerge.form.RepoForm;
import com.teammerge.model.PathModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.ScheduleJobModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.services.GitService;
import com.teammerge.strategy.BlobConversionStrategy;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;
import com.teammerge.utils.StringUtils;
import com.teammerge.validator.BaseValidator.ValidationResult;
import com.teammerge.validator.impl.BranchValidator;
import com.teammerge.validator.impl.CommitDiffValidator;
import com.teammerge.validator.impl.CommitFormValidator;
import com.teammerge.validator.impl.CommitTreeRequestValidator;
import com.teammerge.validator.impl.CompanyFormValidator;
import com.teammerge.validator.impl.CreateNewBranchValidator;
import com.teammerge.validator.impl.CredentialFormValidator;
import com.teammerge.validator.impl.RepoFormValidator;

@Component
@Path("/v2")
public class RestControllerV2 extends AbstractController {

  private static final String WEBSERVICE_KEY_OUTPUT = "output";

  private static final String WEBSERVICE_KEY_DETAILED_REASON = "detailedReason";

  private static final String WEBSERVICE_KEY_REASON = "reason";

  private static final String WEBSERVICE_KEY_RESULT = "result";

  @Resource(name = "gitService")
  private GitService gitService;

  @Resource(name = "commitDiffValidator")
  private CommitDiffValidator diffValidator;

  @Resource(name = "newBranchValidator")
  private CreateNewBranchValidator newBranchValidator;

  @Resource(name = "repoFormValidator")
  private RepoFormValidator repoFormValidator;

  @Resource(name = "commitValidator")
  private CommitFormValidator commitValidator;

  @Resource(name = "branchValidator")
  private BranchValidator branchValidator;

  @Resource(name = "companyValidator")
  private CompanyFormValidator companyValidator;

  @Resource(name = "treeValidator")
  private CommitTreeRequestValidator treeValidator;

  @Resource(name = "credentialValidator")
  private CredentialFormValidator credentialValidator;

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
  public Response getBranchDetails(@PathParam("id") final String branchId) {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    if ((StringUtils.isEmpty(branchId))) {
      finalOutput = "Ticket id should not be empty!";
      populateFailure(result, finalOutput);

    } else {
      List<BranchModel> branchDetailModel =
          getBranchService().getBranchDetailsForBranchLike(branchId);
      if (CollectionUtils.isNotEmpty(branchDetailModel)) {
        String jsonOutput = JacksonUtils.toJson(branchDetailModel);
        finalOutput = convertToFinalOutput(jsonOutput);
        populateSucess(result, finalOutput);
      } else {
        finalOutput = "There is no data for this Branch!";
        populateSucess(result, finalOutput);
      }
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/branch")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveBranchDetails(final BranchForm branch) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = branchValidator.validate(branch);

    if (vr.hasErrors()) {
      branchValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }
    try {
      getBranchService().saveBranch(branch);
      result.put(WEBSERVICE_KEY_RESULT, "Saved Successfully");
    } catch (RevisionSyntaxException e) {
      populateFailure(result, e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/company/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCompanyDetails(@PathParam("id") final String name) {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    if ((StringUtils.isEmpty(name))) {
      finalOutput = "Company name should not be empty";
      populateFailure(result, finalOutput);

    } else {
      Company company = getCompanyService().getCompanyDetails(name);
      String jsonOutput = JacksonUtils.toJson(company);
      finalOutput = convertToFinalOutput(jsonOutput);
      populateSucess(result, finalOutput);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/company")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCompanyDetails(final CompanyForm company) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = companyValidator.validate(company);

    if (vr.hasErrors()) {
      companyValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }

    try {
      getCompanyService().saveCompanyDetails(company);
      result.put(WEBSERVICE_KEY_RESULT, "Saved Successfully");
    } catch (RevisionSyntaxException e) {
      populateFailure(result, e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }


  @GET
  @Path("/schedule/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getScheduleDetails(@PathParam("id") final String jobId) {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    if ((StringUtils.isEmpty(jobId))) {
      finalOutput = "Job id should not be empty!";
      populateFailure(result, finalOutput);

    } else {
      ScheduleJobModel scheduleJobModel = getSchedulerService().getSchedule(jobId);
      String jsonOutput = JacksonUtils.toJson(scheduleJobModel);
      finalOutput = convertToFinalOutput(jsonOutput);
      populateSucess(result, finalOutput);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/addCommit")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCommitDetails(final CommitForm commit) {
    Map<String, Object> result = new HashMap<>();
    ValidationResult vr = commitValidator.validate(commit);

    if (vr.hasErrors()) {
      commitValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }
    try {
      getCommitService().saveOrUpdateCommitDetails(commit);
      result.put(WEBSERVICE_KEY_RESULT, "Saved Successfully");
    } catch (RevisionSyntaxException e) {
      populateFailure(result, e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/commit/{id}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getTicketCommitDetails(@PathParam("id") final String ticketId) {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    if ((StringUtils.isEmpty(ticketId))) {
      finalOutput = "Ticket id should not be empty";
      populateFailure(result, finalOutput);

    } else {
      List<CommitModel> commitModel = getCommitService().getCommitDetails(ticketId);
      String jsonOutput = JacksonUtils.toJson(commitModel);
      finalOutput = convertToFinalOutput(jsonOutput);
      populateSucess(result, finalOutput);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/count/{ticketId}")
  public Response getCommitAndBranchCount(@PathParam("ticketId") final String ticketId) {
    int numOfBranches = 0;
    int numOfCommits = 0;

    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    if ((StringUtils.isEmpty(ticketId))) {
      finalOutput = "Ticket id should not be empty";
      populateFailure(result, finalOutput);

    } else {
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
      populateSucess(result, finalOutput);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/credentials/{company}/{project}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getCredentialsDetails(@PathParam("company") final String company,
      @PathParam("project") final String project) {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    CredentialRequestForm crf = new CredentialRequestForm(company, project);
    ValidationResult vr = credentialValidator.validate(crf);

    if (vr.hasErrors()) {
      credentialValidator.putErrorsInMap(result, vr);
      return Response.status(200).type("application/json").entity(result)
          .header("Access-Control-Allow-Origin", "*").build();
    }

    RepoCredentials repoCredentials = getRepoCredentialService().getCredentialDetails(crf);
    String jsonOutput = JacksonUtils.toJson(repoCredentials);
    finalOutput = convertToFinalOutput(jsonOutput);
    populateSucess(result, finalOutput);

    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @POST
  @Path("/addProject")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({"application/json"})
  public Response addRepository(final RepoForm repoForm) {

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

      String finalOutput = "Project details saved successfully!!";
      populateSucess(result, finalOutput);
    } catch (RevisionSyntaxException e) {
      populateFailure(result, e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("/allRepos/")
  public Response getAllRepoModels() {
    Map<String, Object> result = new HashMap<>();
    String finalOutput = null;

    try {
      List<RepositoryModel> repos = getRepositoryService().getRepositoryModelsFromDB();

      if (CollectionUtils.isNotEmpty(repos)) {
        String jsonOutput = JacksonUtils.toJson(repos);
        finalOutput = convertToFinalOutput(jsonOutput);
      } else {
        finalOutput = "There are no repositories!";
      }

      populateSucess(result, finalOutput);
    } catch (Exception e) {
      populateFailure(result, e);
    }
    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
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
      results = getRepositoryService().createBranch(form.getCompanyId(), form.getProjectId(),
          form.getBranchName(), form.getStartingPoint());

      String resultStatus = (String) results.get(WEBSERVICE_KEY_RESULT);
      result.put(WEBSERVICE_KEY_RESULT, resultStatus);

      if (resultStatus.equalsIgnoreCase("success")) {
        Object createdBranchObj = results.get("branch");
        if (createdBranchObj != null) {
          Ref createdBranch = (Ref) createdBranchObj;
          result.put(WEBSERVICE_KEY_OUTPUT,
              "Branch " + createdBranch.getName() + " created successfully!!");
        }
      } else {
        result.put(WEBSERVICE_KEY_REASON, results.get(WEBSERVICE_KEY_REASON));
        result.put(WEBSERVICE_KEY_DETAILED_REASON, results.get(WEBSERVICE_KEY_DETAILED_REASON));
      }
    } catch (Exception e) {
      populateFailure(result, e);
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

      result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.SUCCESS);
      result.put(WEBSERVICE_KEY_OUTPUT, diffResult);
    } catch (RevisionSyntaxException | IOException e) {
      populateFailure(result, e);
    }

    return Response.status(200).type("application/json").entity(result)
        .header("Access-Control-Allow-Origin", "*").build();
  }

  @GET
  @Path("{repo}/tree/{commitId}/{path}")
  @Produces({"application/json"})
  public Response getFilesInACommit(@PathParam("repo") final String repo,
      @PathParam("commitId") final String commitId, @PathParam("path") final String path) {
    Map<String, Object> result = new HashMap<>();

    String finalPath = path;
    if (path != null)
      finalPath = path.equals("null") ? null : path;

    if (!StringUtils.isEmpty(finalPath)) {
      finalPath = finalPath.replace("!", "/");
    }

    CommitTreeRequestForm form = new CommitTreeRequestForm(repo, commitId, finalPath);
    ValidationResult vr = treeValidator.validate(form);

    if (vr.hasErrors()) {
      treeValidator.putErrorsInMap(result, vr);
      return Response.status(200).entity(result).build();
    }

    try {
      List<PathModel> treeResult = getRepositoryService().getTree2(repo, finalPath, commitId);

      result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.SUCCESS);
      result.put(WEBSERVICE_KEY_OUTPUT, treeResult);

    } catch (IOException e) {
      populateFailure(result, e);
    }

    return Response.status(200).entity(result).build();
  }

  @GET
  @Path("{repo}/blob/{commitId}/{path}")
  @Produces({"application/json"})
  public Response getBlob(@PathParam("repo") final String repo,
      @PathParam("commitId") final String commitId, @PathParam("path") final String path) {
    Map<String, Object> result = new HashMap<>();

    String finalPath = path;
    if (path != null)
      finalPath = path.equals("null") ? null : path;

    if (!StringUtils.isEmpty(finalPath)) {
      finalPath = finalPath.replace("!", "/");
    }

    CommitTreeRequestForm form = new CommitTreeRequestForm(repo, commitId, finalPath);

    ValidationResult vr = treeValidator.validate(form);

    if (vr.hasErrors()) {
      treeValidator.putErrorsInMap(result, vr);
      return Response.status(200).entity(result).build();
    }

    try {
      Map<String, Object> blobResult = getRepositoryService().getBlob(form);

      result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.SUCCESS);
      result.put(WEBSERVICE_KEY_OUTPUT, blobResult.get(BlobConversionStrategy.Key.SOURCE.name()));
    } catch (RevisionSyntaxException | IOException e) {
      populateFailure(result, e);
    }

    return Response.status(200).entity(result).build();
  }

  public void populateSucess(Map<String, Object> result, String output) {
    result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.SUCCESS);
    result.put(WEBSERVICE_KEY_OUTPUT, output);
  }

  public void populateFailure(Map<String, Object> result, String output) {
    result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.FAILURE);
    result.put(WEBSERVICE_KEY_OUTPUT, output);
  }

  public void populateFailure(Map<String, Object> result, Exception e) {
    result.put(WEBSERVICE_KEY_RESULT, WebServiceResult.FAILURE);
    result.put(WEBSERVICE_KEY_REASON, e.getMessage());
    result.put(WEBSERVICE_KEY_DETAILED_REASON, e);
  }

}
