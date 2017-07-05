package com.teammerge.model;

import java.util.List;
import java.util.Map;

public class CustomTicketModel {
	private Integer numOfBranches;
	private Integer numOfCommits;
	private String ticketId;
	private Map<String, List<RepositoryCommit>> branchCommitMap;
	private String repositoryName;
	private String byAuthor;

	@Override
	public String toString() {
		String str = "";
		str += "Repo Name: " + repositoryName;
		str += ", by Author: " + byAuthor;
		str += ", ticket Id:" + ticketId;

		str += "<br><br>";
		return str;
	}

	public String getByAuthor() {
		return byAuthor;
	}

	public void setByAuthor(String byAuthor) {
		this.byAuthor = byAuthor;
	}

	public Integer getNumOfBranches() {
		return numOfBranches;
	}

	public void setNumOfBranches(Integer numOfBranches) {
		this.numOfBranches = numOfBranches;
	}

	public Integer getNumOfCommits() {
		return numOfCommits;
	}

	public void setNumOfCommits(Integer numOfCommits) {
		this.numOfCommits = numOfCommits;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public Map<String, List<RepositoryCommit>> getBranchCommitMap() {
		return branchCommitMap;
	}

	public void setBranchCommitMap(Map<String, List<RepositoryCommit>> branchCommitMap) {
		this.branchCommitMap = branchCommitMap;
	}

}
