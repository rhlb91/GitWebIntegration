package com.teammerge.services.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.FileSettings;
import com.teammerge.manager.RuntimeManager;
import com.teammerge.services.RuntimeService;
import com.teammerge.utils.XssFilter;
import com.teammerge.utils.XssFilter.AllowXssFilter;

@Service("runtimeService")
public class RuntimeServiceImpl implements RuntimeService {

  private static RuntimeManager runtimeManager = null;

  @Value("${git.baseFolder}")
  private String baseFolderPath;

  @Override
  public RuntimeManager getRuntimeManager() {
    if (runtimeManager == null) {
      File baseFolder = new File(baseFolderPath);

      File regFile =
          com.teammerge.utils.FileUtils.resolveParameter(Constants.baseFolder$, baseFolder,
              baseFolderPath);
      FileSettings settings = new FileSettings(regFile.getAbsolutePath());

      // configure the Gitblit singleton for minimal, non-server operation
      XssFilter xssFilter = new AllowXssFilter();
      runtimeManager = new RuntimeManager(settings, xssFilter, baseFolder).start();
    }
    return runtimeManager;
  }
}
