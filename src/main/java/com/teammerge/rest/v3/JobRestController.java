package com.teammerge.rest.v3;

import java.util.*;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Component;


import com.teammerge.rest.AbstractController;


import com.teammerge.cronjob.JobGetCommitDetails;
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
	  @Path("/getbranchcommitDetails")  
	  
	  public Response getBranchCommitDetails(){
		  
		  //JobGetCommitDetails obj = new JobGetCommitDetails();
		  
		  //obj.getBranchCommitDetails();
		  
           CronJob obj = new CronJob();
		  
		  obj.getCronJob();
		  
		  
		  String output = "Get Branch Commit Details has been run sucessfully";
		  
		return Response.status(200).entity(output).header("Access-Control-Allow-Origin", "*").build();
		 }   
	
}//Class









