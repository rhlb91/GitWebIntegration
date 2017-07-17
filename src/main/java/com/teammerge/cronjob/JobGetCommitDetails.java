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
import com.teammerge.rest.AbstractController;
import com.teammerge.services.BranchService;
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
	

	public void execute(JobExecutionContext context)throws JobExecutionException {

		
		 ObjectId object = null;
		  String output = "";
		  
		  logger.info("JobGetCommitDetails start: " + context.getFireTime());
		 	
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
						String branch_name = temp.getName();
						ObjectId branch_id = temp.getReferencedObjectId();
						Date lastmodified_date = temp.getDate();
						ObjectId repository_id = temp.getObjectId();
						String msg = temp.getShortMessage();
						PersonIdent author = temp.getAuthorIdent();
            		                                      
		              int number = Collections.frequency(branchModels, temp);
		             		            			             
		             List<RevCommit> commits = JGitUtils.getRevLog(repository, branch, TimeUtils.getInceptionDate());
		             
		             if (commits!= null) {
		            	
		            	 for (RevCommit commit : commits) {
				           
		            		 String Msgfull = commit.getFullMessage();
		            		 String Msgshort = commit.getShortMessage();
		            		PersonIdent Committer = commit.getCommitterIdent();
		            		Date commitDate = JGitUtils.getCommitDate(commit);
		            		ObjectId commitid = commit.getId();
		            		
                           }
		            	 
		             } else {
		                		                
		                 logger.info("JobGetCommitDetails Unable to find commit!!");
		             }
		          }
		      }	
		    }
		    		    
			logger.info("JobGetCommitDetails next scheduled time: " + context.getNextFireTime());
		 }
	}


