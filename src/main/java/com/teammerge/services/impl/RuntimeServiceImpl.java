package com.teammerge.services.impl;

import java.io.File;

import com.teammerge.Constants;
import com.teammerge.FileSettings;
import com.teammerge.manager.RuntimeManager;
import com.teammerge.services.RuntimeService;
import com.teammerge.utils.XssFilter;
import com.teammerge.utils.XssFilter.AllowXssFilter;

public class RuntimeServiceImpl implements RuntimeService {

  private static RuntimeManager runtimeManager = null;

  @Override
  public RuntimeManager getRuntimeManager() {
    if (runtimeManager == null) {
      File baseFolder = new File(System.getProperty("user.dir"));
      String path = "/home/rahul/Downloads/git/";

      File regFile =
          com.teammerge.utils.FileUtils.resolveParameter(Constants.baseFolder$, baseFolder, path);
      FileSettings settings = new FileSettings(regFile.getAbsolutePath());

      // configure the Gitblit singleton for minimal, non-server operation
      XssFilter xssFilter = new AllowXssFilter();
      runtimeManager = new RuntimeManager(settings, xssFilter, baseFolder).start();
    }
    return runtimeManager;
  }
}
