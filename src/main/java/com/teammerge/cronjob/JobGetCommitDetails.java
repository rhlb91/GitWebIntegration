package com.teammerge.cronjob; 
 
import java.util.*;

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

import com.teammerge.services.impl.CommitServiceImpl;
import com.teammerge.model.RefModel; 
import com.teammerge.model.ExtCommitModel; 
import com.teammerge.model.RepositoryModel; 
import com.teammerge.model.TimeUtils; 
import com.teammerge.rest.AbstractController; 
import com.teammerge.services.BranchService; 
import com.teammerge.services.BranchDetailService;
import com.teammerge.services.CommitService; 
import com.teammerge.services.DashBoardService; 
import com.teammerge.services.RepositoryService; 
import com.teammerge.utils.JGitUtils; 
 
@Component 
public class JobGetCommitDetails extends AbstractController implements Job { 
 
  private final Logger logger = LoggerFactory.getLogger(getClass()); 
   
  @Resource(name = "repositoryService") 
  private RepositoryService repositoryService; 
 
  @Resource(name = "dashBoardService") 
  private DashBoardService dashBoardService; 
 
  @Resource(name = "commitService") 
  private CommitService commitService; 
 
  @Resource(name = "branchService") 
  private BranchService branchService; 
  
  @Resource(name = "branchdetailservice") 
  private BranchDetailService branchdetailservice; 
   
 
  public void execute(JobExecutionContext context)throws JobExecutionException { 
 
	  logger.info("JobGetCommitDetails start: " + context.getFireTime());

		getBranchCommitDetails();

		logger.info("JobGetCommitDetails next scheduled time: "	+ context.getNextFireTime());
	  
	 
     } 
  
  public synchronized  void getBranchCommitDetails() {
	  
	     ObjectId object = null; 
	      String output = ""; 
	       
	     
         
	     // GetBranchModels dd = new GetBranchModels();
		 // List<RefModel> branchModels = dd.getBranchModels();
		  
		  
		  
	      RepositoryService repositoryService = ApplicationContextUtils.getBean(RepositoryService.class); 
		     
		     List<RepositoryModel> repositories = repositoryService.getRepositoryModels(); 
		         
		        for (RepositoryModel repoModel : repositories) { 
		          if (repoModel.isCollectingGarbage()) { 
		            continue; 
		          } 
		 
		       Repository repository = repositoryService.getRepository(repoModel.getName()); 
		           
		    
		       List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);  
		          
	             Set<RefModel> uniqueSet = new HashSet<RefModel>(branchModels); 
	         
	          if (branchModels.size() > 0) { 
	 
	              for (RefModel temp : uniqueSet) { 
	            	  
	            
	                object = temp.getReferencedObjectId(); 
	                 String s1=temp.getName();  
	                 String branch=s1;  
	                   
	            object = temp.getReferencedObjectId();
	          
	                 
	          //  String branchId = temp.getObjectId();
	            
	            String repository_id = repoModel.getName();
	            String branchId = temp.getName();
	            Date lastmodified_date = temp.getDate(); 
	            int num_of_commit = Collections.frequency(branchModels, temp); 
	            int num_of_pull = Collections.frequency(branchModels, temp); 
	            int num_of_branches = Collections.frequency(branchModels, temp); 
	            
	                   
	            
	            }    
	            
	            
	            
	            
	            
	            
	            
	            
	            
	            // BranchService bs = new BranchService();
	            
	            //BranchDetailService.createBranch(branch);
	                                                       
	         
	                                                     
//                 List<RevCommit> commits = JGitUtils.getRevLog(repository, branch, TimeUtils.getInceptionDate()); 
//                  
//                 if (commits!= null) { 
//	                   
//	                   for (RevCommit commit : commits) { 
//	                    
//	                     String Msgfull = commit.getFullMessage(); 
//	                     String Msgshort = commit.getShortMessage(); 
//	                    PersonIdent Committer = commit.getCommitterIdent(); 
//	                    Date commitDate = JGitUtils.getCommitDate(commit); 
//                         ObjectId commitid = commit.getId(); 
//	                     
//	              } 
	                    
	               
	                 //}
	                 
	              
	             
	  
	  
	              }}
	        
  }
  }
  
	