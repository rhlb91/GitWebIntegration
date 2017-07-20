package com.teammerge.rest.v2;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.teammerge.entity.Company;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.CommitForm;
import com.teammerge.form.RepoForm;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.rest.AbstractController;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.JacksonUtils;

@Component
@Path("/v2")
public class RestControllerV2 extends AbstractController {

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
  public Response getBranchDetails(@PathParam("id") String branchId) {
    BranchDetailModel branchDetailModel = getBranchDetailService().getBranchDetails(branchId);
    String jsonOutput = JacksonUtils.toBranchDetailJson(branchDetailModel);
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
  public Response getCompanyDetails(@PathParam("id") String name) {
    Company company = getCompanyDetailService().getCompanyDetails(name);
    String jsonOutput = JacksonUtils.toCompanyDetailJson(company);
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
  public Response getTicketCommitDetails(@PathParam("id") String commitId) {
    CommitModel commitModel = getCommitService().getCommitDetails(commitId);
    String jsonOutput = JacksonUtils.toBranchCommitDetailJson(commitModel);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }
  
/**
 * This method is used get all commits Detail list from Dao layer
 * @return list of commits list in Json format
 */
  
  @GET
  @Path("/commitDetails")
  public Response getAllTicketCommitDetails() {
    List<CommitModel> commitModel = getCommitService().getCommitDetailsAll();
    String jsonOutput = JacksonUtils.toAllCommitDetailJson(commitModel);
    String finalOutput = convertToFinalOutput(jsonOutput);

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }
  
  @GET
  @Path("/count/{commitId}")
  public Response getCommitAndBranchCount(@PathParam("commitId") String commits) {
    String finalOutput = "";
    BranchDetailModel branchDetailModel = getBranchDetailService().getBranchDetails(commits);
       finalOutput =
        convertToFinalOutput("{\"numOfPull\": " + branchDetailModel.getNumOfPull() + ","
            + "\"numOfCommits\": " + branchDetailModel.getNumOfCommits() + "}");

    return Response.status(200).entity(finalOutput).header("Access-Control-Allow-Origin", "*")
        .build();
  }


  @GET
  @Path("/credentials/{user}")
  public Response getCredentialsDetails(@PathParam("user") String name) {
    RepoCredentials repoCredentials = getRepoCredentialService().getCredentialDetails(name);
    String jsonOutput = JacksonUtils.toCredentialDetailJson(repoCredentials);
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
}
