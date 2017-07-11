package com.teammerge.rest.model;

public class Ticketmodel {

    private String ticketID;

    public int getNum_commit() {
        return num_commit;
    }

    public void setNum_commit(int num_commit) {
        this.num_commit = num_commit;
    }

    public int getNum_pull() {
        return num_pull;
    }

    public void setNum_pull(int num_pull) {
        this.num_pull = num_pull;
    }

    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public int getNum_branches() {
        return num_branches;
    }

    public void setNum_branches(int num_branches) {
        this.num_branches = num_branches;
    }

    public String getCommit_message() {
        return commit_message;
    }

    public void setCommit_message(String commit_message) {
        this.commit_message = commit_message;
    }

    private int num_commit;
    private int num_pull;
    private String commitID;

    private int num_branches;
    private String commit_message;

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

}