package com.teammerge.model;

import java.util.List;

public class TicketCommitsModel {
	private List<ExtCommitModel> commits;

	public List<ExtCommitModel> getCommits() {
		return commits;
	}

	public void setCommits(List<ExtCommitModel> commits) {
		this.commits = commits;
	}
}
