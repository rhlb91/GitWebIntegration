package com.teammerge.model;

import org.eclipse.jgit.lib.PersonIdent;

public class CommitModel {
	private PersonIdent commitAuthor;
	private String shortMessage;
	private String trimmedMessage;
	private String commitHash;
	private String name;

	@Override
	public String toString() {
		String str = "[";
		str += "Name: " + name;
		str += ", commit Author: " + commitAuthor;
		str += ", short Msg: " + shortMessage;
		str += ", trimmed Msg: " + trimmedMessage;
		str += ", commit Hash: " + commitHash;
		str += "]\n";
		return str;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public String getTrimmedMessage() {
		return trimmedMessage;
	}

	public void setTrimmedMessage(String trimmedMessage) {
		this.trimmedMessage = trimmedMessage;
	}

	public String getCommitHash() {
		return commitHash;
	}

	public void setCommitHash(String commitHash) {
		this.commitHash = commitHash;
	}

	public PersonIdent getCommitAuthor() {
		return commitAuthor;
	}

	public void setCommitAuthor(PersonIdent commitAuthor) {
		this.commitAuthor = commitAuthor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
