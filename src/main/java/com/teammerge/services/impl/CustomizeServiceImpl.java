package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.model.CommitModel;
import com.teammerge.model.CustomTicketModel;
import com.teammerge.model.DailyLogEntry;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.model.RepositoryModel;
import com.teammerge.services.CustomizeService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.CommitCache;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

@Service("customizeService")
public class CustomizeServiceImpl implements CustomizeService {

	@Resource(name = "repositoryService")
	RepositoryService repositoryService;

	public CustomTicketModel getDetailsForBranchName(String branchName, String repoNme) {
		final RevObject referencedObject = null;

		CustomTicketModel customTicketModel = new CustomTicketModel();

		Map<String, List<RepositoryCommit>> commitsPerBranch = new HashMap<>();
		int numOfMatchedBranches = 0;
		int numOfTotalCommits = 0;

		List<RepositoryModel> repositories = repositoryService.getRepositoryModels();

		Calendar c = Calendar.getInstance();
		c.setTime(new Date(2));
		Date minimumDate = c.getTime();

		for (RepositoryModel model : repositories) {
			if (model.isCollectingGarbage()) {
				continue;
			}

			Repository repository = repositoryService.getRepositoryManager().getRepository(repoNme);

			List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, true, -1);

			if (branchModels.size() > 0) {
				for (RefModel branch : branchModels) {
					if (branch.getName().contains(branchName)) {
						numOfMatchedBranches = numOfMatchedBranches + 1;

						if (model.isHasCommits()) {
							if (repository != null) {

								// TODO use caching to get daily log entries -
								// as it will be a heavy task if commits
								// increases
								// List<DailyLogEntry> dailyLogEntries =
								// RefLogUtils.getDailyLogByRef(model.getName(),repository,
								// minimumDate, timezone);
								/*if (referencedObject instanceof RevCommit) {
								RevCommit commit = (RevCommit) referencedObject;
								Date date = JGitUtils.getAuthorDate(commit);*/
								
								List<RepositoryCommit> repoCommitsPerBranch = CommitCache.instance()
										.getCommits(model.getName(), repository, branch.getName(), minimumDate);

								commitsPerBranch.put(branch.getName(), repoCommitsPerBranch);
							}
							//}
						}
					}
				}
			}
		}

		for (List<RepositoryCommit> commits : commitsPerBranch.values()) {
			numOfTotalCommits += commits.size();
		}
		customTicketModel.setNumOfBranches(numOfMatchedBranches);
		customTicketModel.setNumOfCommits(numOfTotalCommits);
		customTicketModel.setBranchCommitMap(commitsPerBranch);
		customTicketModel.setTicketId(branchName);
		return customTicketModel;
	}

	
	public List<CommitModel> populateCommits(List<RepositoryCommit> commits, DailyLogEntry change) {
		List<CommitModel> populatedCommits = new ArrayList<>();
		for (RepositoryCommit commit : commits) {
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
			commitModel.setName(commit.getName());
			if (commitModel.getShortMessage().startsWith("Merge")) {
				commitModel.setIsMergeCommit(true);
			} else {
				commitModel.setIsMergeCommit(false);
			}
			populatedCommits.add(commitModel);
		}
		return populatedCommits;
	}
}
