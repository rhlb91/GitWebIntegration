package com.teammerge.services.impl;

import java.io.File;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.FileSettings;
import com.teammerge.manager.RuntimeManager;
import com.teammerge.services.RuntimeService;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.XssFilter;
import com.teammerge.utils.XssFilter.AllowXssFilter;

@Service("runtimeService")
public class RuntimeServiceImpl implements RuntimeService {

  private static RuntimeManager runtimeManager = null;

  @Value("${app.propertiesFile.name}")
  private String propertiesFileName;

  @Value("${git.repository.folderName}")
  private String repoFolderName;

  private String baseFolderPath;

  @Override
  public RuntimeManager getRuntimeManager() {
    if (runtimeManager == null) {
      File baseFolder = new File(getBaseFolderPath());
      File repoFolder = new File(ApplicationDirectoryUtils.getProgramDirectory(), repoFolderName);

      FileSettings settings = new FileSettings(getPropertiesFile().getAbsolutePath());


      // configure the Gitblit singleton for minimal, non-server operation
      XssFilter xssFilter = new AllowXssFilter();

      Map<String, File> paths = new HashedMap<>();
      runtimeManager = new RuntimeManager(settings, xssFilter, baseFolder, repoFolder).start();
    }
    return runtimeManager;
  }

  public String getBaseFolderPath() {
    if (baseFolderPath == null) {
      baseFolderPath = ApplicationDirectoryUtils.getProgramDirectory();
    }
    return baseFolderPath;
  }

  public File getPropertiesFile() {
    File propertiesFile = new File(getBaseFolderPath(), propertiesFileName);
    return com.teammerge.utils.FileUtils.resolveParameter(Constants.baseFolder$, propertiesFile,
        propertiesFile.getAbsolutePath());
  }

}
