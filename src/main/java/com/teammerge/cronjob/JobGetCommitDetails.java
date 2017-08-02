package com.teammerge.cronjob;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.Repository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.teammerge.Constants;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchDetailService;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;
@Component
public class JobGetCommitDetails implements Job {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "repositoryService")
	private RepositoryService repositoryService;


	@Resource(name = "commitService")
	private CommitService commitService;

	
	@Resource(name = "branchDetailService")
	private BranchDetailService branchDetailService;

	@Value("${git.commit.timeFormat}")
	  private String commitTimeFormat;

	  @Value("${app.dateFormat}")
	  private String commitDateFormat;
	
	
	
	public void execute(JobExecutionContext context)throws JobExecutionException {

		logger.info("JobGetCommitDetails start: " + context.getFireTime());
		
		Properties prop = new Properties();
		InputStream input = null;
		OutputStream output = null;

		try {

			input = new FileInputStream("/home/reflex/Documents/config.properties");

			// load a properties file
			prop.load(input);

	// get the property value and print it out
		//String lastjobruntime = prop.getProperty("LastJobRunTime");
		String lrj = prop.getProperty("LastJobRunTime");
		
		System.out.println("LastJobRunTime--->"+lrj);
		
		//Job Method
		getBranchCommitDetails(lrj);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Date lr = context.getPreviousFireTime();
		//String lrj = lr.toString();
		
		
		//For set LastJobRunTime
		try {

			output = new FileOutputStream("/home/reflex/Documents/config.properties");

			// set the properties value
			
			Date FireTime = context.getFireTime();
			String LastJobRunTime = FireTime.toString();
			prop.setProperty("LastJobRunTime",LastJobRunTime);
			
			// save properties to project root folder
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		logger.info("JobGetCommitDetails next scheduled time:"	+ context.getNextFireTime());

	}

	public synchronized void getBranchCommitDetails(String lrj) {
		
		System.out.println("LastJobRunTime--->"+lrj);
		
		repositoryService = ApplicationContextUtils.getBean(RepositoryService.class);

		branchDetailService = ApplicationContextUtils.getBean(BranchDetailService.class);
		
		commitService = ApplicationContextUtils.getBean(CommitService.class);
		
		Date minimumDate = TimeUtils.getInceptionDate();
		
		List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

		for (RepositoryModel repoModel : repositories) {
			if (repoModel.isCollectingGarbage()) {
				continue;
			}

			Repository repository = repositoryService.getRepository(repoModel.getName());

			List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

			Set<RefModel> uniqueSet = new HashSet<RefModel>(branchModels);

			if (branchModels.size() > 0) {

				for (RefModel branch : uniqueSet) {

					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Date lastmodifieddate = branch.getDate();
					String lastmodified_date = df.format(lastmodifieddate);
					
					
					List<RepositoryCommit> commits = CommitCache.instance().getCommits(repoModel.getName(), repository, branch.getName(), minimumDate);
					
					// Data Transferred in BranchDetailModel--Start
					BranchDetailModel Bmodel = new BranchDetailModel();
					Bmodel.setBranchId(branch.getName());
					Bmodel.setLastModifiedDate(lastmodified_date);
					Bmodel.setNumOfCommits(commits.size());
					Bmodel.setNumOfPull(1);
					Bmodel.setRepositoryId(repoModel.getName());

					branchDetailService.saveBranch(Bmodel);
							
					// Data Transferred in BranchDetailModel--End

					// Code related to Commits---Start

					if (commits != null) {

						for (RepositoryCommit commit : commits) {
					
					//To Compare Last Run Job time with Commit Date.
					Date commitdate = commit.getCommitDate();
					String cdate = commitdate.toString();
					if (lrj.compareTo(cdate) < 0) {
			          								
							CommitModel commitModel = new CommitModel();
							
							 commitModel.setCommitAuthor(commit.getAuthorIdent());
						      // short message
						      String shortMessage = commit.getShortMessage();
						      String trimmedMessage = shortMessage;
						      if (commit.getRefs() != null && commit.getRefs().size() > 0) {
						        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG_REFS);
						      } else {
						        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG);
						      }
						      commitModel.setShortMessage(shortMessage);
						      commitModel.setTrimmedMessage(trimmedMessage);

					      // commit hash link
					      int hashLen = 6;
					      if (commit.getName() != null) {
					        commitModel.setCommitHash(commit.getName().substring(0, hashLen));
						      }
					      commitModel.setCommitId(commit.getName());
					      if (commitModel.getShortMessage().startsWith("Merge")) {
					        commitModel.setIsMergeCommit(true);
						      } else {
						        commitModel.setIsMergeCommit(false);
						      }

					      commitModel.setCommitDate(commit.getCommitDate());
						     // commitModel.setCommitTimeFormatted(TimeUtils.convertToDateFormat(commit.getCommitDate(), commitTimeFormat));
						      commitModel.setCommitTimeFormatted("1:00 AM");
						      
						      commitModel.setBranchName(branch.getName());
						      commitModel.setRepositoryName(repoModel.getName());
					
							commitService.saveCommit(commitModel);
					 }
						}

				}
					// Code related to Commits---End

				} // Branch Traverse

			}// BranchModel Size
		}// Repo

	}// Method
}// Class

