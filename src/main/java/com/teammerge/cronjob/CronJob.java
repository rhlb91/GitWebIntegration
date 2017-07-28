package com.teammerge.cronjob;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.teammerge.dao.BaseDao;
import com.teammerge.form.CommitForm;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.model.CommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.TimeUtils;
import com.teammerge.services.BranchDetailService;
import com.teammerge.services.CommitService;
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

	@Resource(name = "commitService")
	private CommitService commitService;

	
	public synchronized void getCronJobForBranch() {
		System.out.println("Enter in getCronJobForBranch");

		repositoryService = ApplicationContextUtils
				.getBean(RepositoryService.class);

		branchDetailService = ApplicationContextUtils
				.getBean(BranchDetailService.class);

		List<RepositoryModel> repositories = repositoryService
				.getRepositoryModels();

		for (RepositoryModel repoModel : repositories) {
			if (repoModel.isCollectingGarbage()) {
				continue;
			}

			Repository repository = repositoryService.getRepository(repoModel
					.getName());

			List<RefModel> branchModels = JGitUtils.getRemoteBranches(
					repository, true, -1);

			Set<RefModel> uniqueSet = new HashSet<RefModel>(branchModels);

			if (branchModels.size() > 0) {

				for (RefModel temp : uniqueSet) {

					DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Date lastmodifieddate = temp.getDate();
					String lastmodified_date = df.format(lastmodifieddate);
					List<RevCommit> commits = JGitUtils.getRevLog(repository,temp.displayName, TimeUtils.getInceptionDate());

					BranchDetailModel Bmodel = new BranchDetailModel();
					Bmodel.setLastModifiedDate(lastmodified_date);
					Bmodel.setNumOfCommits(commits.size());
					Bmodel.setNumOfPull(1);
					Bmodel.setRepositoryId(repoModel.getName());
					Bmodel.setBranchId(temp.displayName);

					branchDetailService.saveBranch(Bmodel);

				}

			}
		}

	}

	public synchronized void getCronJobForCommit() {
		System.out.println("Enter in getCronJobForCommit");

		repositoryService = ApplicationContextUtils
				.getBean(RepositoryService.class);

		branchDetailService = ApplicationContextUtils
				.getBean(BranchDetailService.class);

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

					List<RevCommit> commits = JGitUtils.getRevLog(repository,
							temp.displayName, TimeUtils.getInceptionDate());

					if (commits != null) {

						for (RevCommit commit : commits) {

							CommitModel model = new CommitModel();

							model.setCommitId(commit.getName());
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

				}

			}

		}

	}

}
