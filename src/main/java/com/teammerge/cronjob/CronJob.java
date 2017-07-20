package com.teammerge.cronjob; 
 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchDetailService;
//import com.teammerge.services.BranchService; 
//import com.teammerge.services.CommitService; 
//import com.teammerge.services.DashBoardService; 
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.HibernateUtils;
import com.teammerge.utils.JGitUtils;
 
@Component 
public class CronJob { 
 
  private final Logger logger = LoggerFactory.getLogger(getClass()); 
   
  @Resource(name = "repositoryService") 
  private RepositoryService repositoryService;
  
  @Resource(name = "branchDetailService")
  private BranchDetailService branchDetailService;
 
//  @Resource(name = "dashBoardService") 
//  private DashBoardService dashBoardService; 
// 
//  @Resource(name = "commitService") 
//  private CommitService commitService; 
// 
//  @Resource(name = "branchService") 
//  private BranchService branchService; 
//   
 
//  public void execute(JobExecutionContext context)throws JobExecutionException { 
// 
//	  logger.info("JobGetCommitDetails start: " + context.getFireTime());
//
//	  getCronJob();
//
//		logger.info("JobGetCommitDetails next scheduled time: "	+ context.getNextFireTime());
//	  
//	 
//     } 
  
  public synchronized void getCronJobForBranch() {
	  System.out.println("Enter in getCronJobForBranch");
	
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
            	
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date lastmodifieddate = temp.getDate(); 
            String lastmodified_date = df.format(lastmodifieddate);
           
            int num_of_commit = Collections.frequency(branchModels, temp); 
            int num_of_pull = Collections.frequency(branchModels, temp); 
                           
            BranchDetailModel model = new BranchDetailModel();
            
            model.setBranchId(temp.displayName);
            model.setRepositoryId(repoModel.getName());
            model.setLastModifiedDate(lastmodified_date);
            model.setNumOfCommits(num_of_commit);
            model.setNumOfPull(num_of_pull);
            
            branchDetailService.createBranch(model);
                      
            /*HibernateUtils.openCurrentSessionwithTransaction();
            HibernateUtils.getCurrentSession().saveOrUpdate(model);
            HibernateUtils.closeCurrentSessionwithTransaction();
            */
              }    
	 
              }
          }
	  
  }
  
  public synchronized void getCronJobForCommit() {
	  System.out.println("Enter in getCronJobForCommit");
	 	 
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
            	  
           
            	  List<RevCommit> commits = JGitUtils.getRevLog(repository, temp.displayName, TimeUtils.getInceptionDate()); 
                
             if (commits!= null) { 
                
	                   for (RevCommit commit : commits) { 
	                	   
	                	    CommitModel model = new CommitModel();
	                       
	                	    model.setCommitId(commit.getShortMessage());
	                	    model.setCommitAuthor(commit.getAuthorIdent());
	                	    model.setBranchName(temp.displayName);
	                	    model.setShortMessage(commit.getShortMessage());
	                	   // model.setTrimmedMessage();
	                	    model.setCommitDate(JGitUtils.getCommitDate(commit));
	                	   // model.setCommitHash(commit.get);
	                	   // model.setCommitTimeFormatted(commit.getCommitTime());
	                	    //model.setIsMergeCommit();
	                	    model.setRepositoryName(repoModel.getName());
	                	    	                       
	                       HibernateUtils.openCurrentSessionwithTransaction();
	                       HibernateUtils.getCurrentSession().saveOrUpdate(model);
	                       HibernateUtils.closeCurrentSessionwithTransaction();   
	               
	              } 
	                    
               
                 }        
         
              }    
	 
              }
          }
	  
  }
  
  
  }
	