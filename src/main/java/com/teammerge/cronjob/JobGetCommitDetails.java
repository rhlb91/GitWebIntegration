package com.teammerge.cronjob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchDetailService;
import com.teammerge.services.BranchService;
import com.teammerge.services.CommitService;
import com.teammerge.services.DashBoardService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.HibernateUtils;
import com.teammerge.utils.JGitUtils;

@Component
public class JobGetCommitDetails implements Job {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource(name = "repositoryService")
	private RepositoryService repositoryService;


	@Resource(name = "commitService")
	private CommitService commitService;

	
	@Resource(name = "branchDetailService")
	private BranchDetailService branchDetailService;

		
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		logger.info("JobGetCommitDetails start: " + context.getFireTime());

		getBranchCommitDetails();

		logger.info("JobGetCommitDetails next scheduled time: "
				+ context.getNextFireTime());

	}

	public synchronized void getBranchCommitDetails() {
		
		
		repositoryService = ApplicationContextUtils.getBean(RepositoryService.class);

		branchDetailService = ApplicationContextUtils.getBean(BranchDetailService.class);
		
		commitService = ApplicationContextUtils.getBean(CommitService.class);
		
		
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
					List<RevCommit> commits = JGitUtils.getRevLog(repository,temp.displayName, TimeUtils.getInceptionDate());

					// Data Transferred in BranchDetailModel--Start
					BranchDetailModel Bmodel = new BranchDetailModel();
					Bmodel.setBranchId(temp.displayName);
					Bmodel.setLastModifiedDate(lastmodified_date);
					Bmodel.setNumOfCommits(commits.size());
					Bmodel.setNumOfPull(1);
					Bmodel.setRepositoryId(repoModel.getName());

					branchDetailService.saveBranch(Bmodel);
							
					// Data Transferred in BranchDetailModel--End

					// Code related to Commits---Start

					if (commits != null) {

						for (RevCommit commit : commits) {
														
							CommitModel model = new CommitModel();
							model.setCommitId(commit.getShortMessage());
							model.setCommitAuthor(commit.getAuthorIdent());
							model.setBranchName(temp.displayName);
							model.setShortMessage(commit.getShortMessage());
							model.setTrimmedMessage(commit.getShortMessage());
							model.setCommitDate(JGitUtils.getCommitDate(commit));
							model.setCommitHash("rtttt");
							model.setCommitTimeFormatted("1:00 AM");
							model.setIsMergeCommit(true);
							model.setRepositoryName(repoModel.getName());

							commitService.saveCommit(model);
							
						}

					}
					// Code related to Commits---End

				} // Branch Traverse

			}// BranchModel Size
		}// Repo

	}// Method
}// Class

