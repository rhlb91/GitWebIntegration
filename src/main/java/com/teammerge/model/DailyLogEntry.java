package com.teammerge.model;

import java.io.Serializable;
import java.util.Date;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.ReceiveCommand;

/**
 * Model class to simulate a push for presentation in the push log news feed
 * for a repository that does not have a Gitblit push log.  Commits are grouped
 * by date and may be additionally split by ref.
 *
 */
public class DailyLogEntry extends RefLogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	public DailyLogEntry(String repository, Date date) {
		super(repository, date, new UserModel("digest"));
	}

	public DailyLogEntry(String repository, Date date, UserModel user) {
		super(repository, date, user);
	}

	@Override
	public PersonIdent getCommitterIdent() {
		if (getAuthorCount() == 1) {
			return getCommits().get(0).getCommitterIdent();
		}

		return super.getCommitterIdent();
	}

	@Override
	public PersonIdent getAuthorIdent() {
		if (getAuthorCount() == 1) {
			return getCommits().get(0).getAuthorIdent();
		}

		return super.getAuthorIdent();
	}

	/**
	 * Tracks the change type for the specified ref.
	 *
	 * @param ref
	 * @param type
	 * @param oldId
	 * @param newId
	 */
	@Override
	public void updateRef(String ref, ReceiveCommand.Type type, String oldId, String newId) {
		// daily digests are filled from most recent to oldest
		String preservedNewId = getNewId(ref);
		if (preservedNewId == null) {
			// no preserved new id, this is newest commit
			// for this ref
			preservedNewId = newId;
		}
		refUpdates.put(ref, type);
		refIdChanges.put(ref, oldId + "-" + preservedNewId);
	}

}
