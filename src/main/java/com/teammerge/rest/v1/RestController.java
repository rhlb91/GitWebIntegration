package com.teammerge.rest.v1;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.teammerge.Constants;
import com.teammerge.FileSettings;
import com.teammerge.manager.RepositoryManager;
import com.teammerge.manager.RuntimeManager;
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
	public Response getRepositories() {
		File baseFolder = new File(System.getProperty("user.dir"));
		String path = "/home/rahul/Downloads/git/";
		
		File regFile = com.teammerge.utils.FileUtils.resolveParameter(Constants.baseFolder$, baseFolder, path);
		FileSettings settings = new FileSettings(regFile.getAbsolutePath());
		
		// configure the Gitblit singleton for minimal, non-server operation
		XssFilter xssFilter = new AllowXssFilter();

		RuntimeManager runtime = new RuntimeManager(settings, xssFilter,
				baseFolder).start();

		RepositoryManager manager = new RepositoryManager(runtime, null);
		
		List<String> list =manager.getRepositoryList();
		
		String output = "hello";
		return Response.status(200).entity(list.toArray()).build();
	}
}
