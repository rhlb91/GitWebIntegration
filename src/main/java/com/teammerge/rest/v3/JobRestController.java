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



import com.teammerge.services.RepositoryService;
import com.teammerge.utils.JGitUtils;
import com.teammerge.cronjob.CronJob;

@Component
@Path("/v3")
public class JobRestController extends  AbstractController{//class
	
	@Resource(name = "cronJob")
	private CronJob cronJob;
	
			  
	  @GET
	  @Path("/")
	  public Response hello() {
	    return Response.status(200).entity("Hi Rest Cron Job working fine!!").build();
	  }
	  
  	  
	  @GET
	  @Path("/UpdateAllBranchs")  
	  
	  public Response getCronJobForBranch(){
		  
		  //CronJob obj = new CronJob();
		  
		  cronJob.runJobSavingForBranchDetails();
		  
		  
		  String output = "Get Branch  Details has been run sucessfully";
		  
		return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
		 }
	  
	  
	  @GET
	  @Path("/UpdateAllCommits")  
	  
	  public Response getCronJobForCommit(){
		  
		 	  
         //  CronJob obj = new CronJob();
		  
		  cronJob.runJobSavingForCommitDetails();
		  
		  
		  String output = "Get Commit Details has been run sucessfully";
		  
		return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
		 }  
	  
}//Class









