package com.teammerge.cronjob;


import java.util.Collections; 
import java.util.Date; 
import java.util.HashSet; 
import java.util.List; 
import java.util.Set; 

import javax.annotation.Resource; 

import org.eclipse.jgit.lib.ObjectId; 
import org.eclipse.jgit.lib.PersonIdent; 
import org.eclipse.jgit.lib.Repository; 
import org.eclipse.jgit.revwalk.RevCommit; 
import org.quartz.Job; 
import org.quartz.JobExecutionContext; 
import org.quartz.JobExecutionException; 
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Component; 
import org.springframework.context.ApplicationContext; 

import com.teammerge.model.RefModel; 
import com.teammerge.model.RepositoryModel; 
import com.teammerge.model.TimeUtils; 
 
import com.teammerge.services.BranchService; 
import com.teammerge.services.CommitService; 
import com.teammerge.services.DashBoardService; 
import com.teammerge.services.RepositoryService; 
import com.teammerge.utils.JGitUtils; 
 
@Component 
public class GetBranchModels { 
 
  private final Logger logger = LoggerFactory.getLogger(getClass()); 
   
  @Resource(name = "repositoryService") 
  private RepositoryService repositoryService; 
 
  @Resource(name = "dashBoardService") 
  private DashBoardService dashBoardService; 
 
  @Resource(name = "commitService") 
  private CommitService commitService; 
 
  @Resource(name = "branchService") 
  private BranchService branchService; 
   
    
  public List<RefModel> getBranchModels() {
	  

       List<RefModel> branchModels = null;
	      
	        
	    RepositoryService repositoryService = ApplicationContextUtils.getBean(RepositoryService.class); 
	     
	     List<RepositoryModel> repositories = repositoryService.getRepositoryModels(); 
	         
	        for (RepositoryModel repoModel : repositories) { 
	          if (repoModel.isCollectingGarbage()) { 
	            continue; 
	          } 
	 
	          Repository repository = repositoryService.getRepository(repoModel.getName()); 
	           
	    
	          branchModels = JGitUtils.getRemoteBranches(repository, true, -1);  
	        
	                 
	     }
	        
return branchModels;
}
}