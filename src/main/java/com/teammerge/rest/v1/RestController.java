package com.teammerge.rest.v1;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.teammerge.Constants;
import com.teammerge.FileSettings;
import com.teammerge.manager.IRepositoryManager;
import com.teammerge.manager.RepositoryManager;
import com.teammerge.manager.RuntimeManager;
import com.teammerge.model.RefModel;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.XssFilter;
import com.teammerge.utils.XssFilter.AllowXssFilter;

@Path("/v1")
public class RestController {

	@GET
	@Path("/hello/{param}")
	public Response getMsg(@PathParam("param") String msg) {

		String output = "Jersey say : " + msg;

		return Response.status(200).entity(output).build();

	}

	@GET
	@Path("/repositories")
	public Response getRepositoriesName() {
		List<String> list = getRepositoryManager().getRepositoryList();
		System.out.println("\n\n " + list.size() + "\n\n");
		for (String list1 : list) {
			System.out.println("\n\n " + list1 + "\n\n");
		}
		String output = "hello";
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/repository/gitlist")
	public Response getRepoForBrowser() {
		Repository repo = getRepository();
		String output = null;
		if (repo == null) {
			output = "Error in loading repository 'Gitlist'";
			return Response.status(200).entity(output).build();
		}
		output = "Repository 'gitlist' successfully loaded!!";
		System.out.println("Repo: " + repo);
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/repository/commit")
	public Response getCommit() {
		String output = null;
		Repository repo = getRepository();

		RevCommit commit = JGitUtils.getCommit(repo, null);
		Date commitDate = JGitUtils.getCommitDate(commit);
		System.out.println("Commit: " + commit);
		System.out.println("Message: " + commit.getFullMessage());
		System.out.println("Name:" + commit.getName());
		output = "Commit Date: " + commitDate;
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/repository/commit/{branch}")
	public Response getAllCommits(@PathParam("branch") String branch) {
		Repository repo = getRepository();

		List<RevCommit> commits = JGitUtils.getRevLog(repo, branch, new Date());
		String output = null;
		if (CollectionUtils.isNotEmpty(commits)) {
			for (RevCommit commit : commits) {
				output += commit.getFullMessage() + "<br>";
			}
		} else {
			output = "No commits found for the branch: " + branch;
		}
		return Response.status(200).entity(output).build();
	}

	@GET
	@Path("/repository/branches")
	public Response getAllBranches() {
		ObjectId object = null;
		String output = null;

		Repository repo = getRepository();
		List<RefModel> branchModels = JGitUtils.getRemoteBranches(repo, true,
				-1);
		if (branchModels.size() > 0) {

			for (RefModel branch : branchModels) {
				object = branch.getReferencedObjectId();
				output += branch.getName() + "--" + object + "<br>";
			}
		}

		return Response.status(200).entity(output).build();

	}

	protected Repository getRepository() {
		String repositoryName = "gitlist";
		Repository r = getRepositoryManager().getRepository(repositoryName);
		if (r == null) {
			System.out.println("\n\nCannot Load Repository" + " "
					+ repositoryName);
			return null;
		}
		return r;
	}

	protected IRepositoryManager getRepositoryManager() {
		File baseFolder = new File(System.getProperty("user.dir"));
		String path = "/home/rahul/Downloads/git/";

		File regFile = com.teammerge.utils.FileUtils.resolveParameter(
				Constants.baseFolder$, baseFolder, path);
		FileSettings settings = new FileSettings(regFile.getAbsolutePath());

		// configure the Gitblit singleton for minimal, non-server operation
		XssFilter xssFilter = new AllowXssFilter();
		RuntimeManager runtime = new RuntimeManager(settings, xssFilter,
				baseFolder).start();
		RepositoryManager manager = new RepositoryManager(runtime, null);

		return manager;
	}
}