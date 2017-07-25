package com.teammerge.rest.v3;

import java.util.*;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Component;

import com.teammerge.model.TimeUtils;
import com.teammerge.rest.AbstractController;



import com.teammerge.utils.JGitUtils;
import com.teammerge.cronjob.CronJob;

@Component
@Path("/v3")
public class JobRestController extends  AbstractController{//class
	
		  
	  @GET
	  @Path("/")
	  public Response hello() {
	    return Response.status(200).entity("Hi Rest Cron Job working fine!!").build();
	  }
	  
  	  
	  @GET
	  @Path("/getCronJobForBranch")  
	  
	  public Response getCronJobForBranch(){
		  
		  //JobGetCommitDetails obj = new JobGetCommitDetails();
		  
		  //obj.getBranchCommitDetails();
		  
           CronJob obj = new CronJob();
		  
		  obj.getCronJobForBranch();
		  
		  
		  String output = "Get Branch  Details has been run sucessfully";
		  
		return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
		 }
	  
	  
	  @GET
	  @Path("/getCronJobForCommit")  
	  
	  public Response getCronJobForCommit(){
		  
		  //JobGetCommitDetails obj = new JobGetCommitDetails();
		  
		  //obj.getBranchCommitDetails();
		  
           CronJob obj = new CronJob();
		  
		  obj.getCronJobForCommit();
		  
		  
		  String output = "Get Commit Details has been run sucessfully";
		  
		return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
		 }  
	  
	  @GET
	  @Path("/tree/{repository}/{commitId}")
	  public Response getTreeDetails(@PathParam("repository") String repoName,
	      @PathParam("commitId") String commitId) {
		   
		  
         CronJob obj = new CronJob();
		  
		  obj.getTreeDetails(repoName,commitId);
		  
		  
		  String Output = "call tree job";
	  
	    return Response.status(200).entity(Output).build();
	  }
	  
	  
	  
	  
	  
	  
	  
	  
	
}//Class









