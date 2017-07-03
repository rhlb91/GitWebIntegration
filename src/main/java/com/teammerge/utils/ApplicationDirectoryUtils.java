package com.teammerge.utils;

import java.io.File;
import java.net.URISyntaxException;

public class ApplicationDirectoryUtils {
  private static String getJarName() {
    return new File(ApplicationDirectoryUtils.class.getProtectionDomain().getCodeSource()
        .getLocation().getPath()).getName();
  }

  private static boolean runningFromJAR() {
    String jarName = getJarName();
    return jarName.contains(".jar");
  }

  private static boolean runningFromWAR() {
    String jarName = getJarName();
    return jarName.contains(".war");
  }

  public static String getProgramDirectory() {
   /* List<String> result = new ArrayList<String>();*/
    
    if (runningFromJAR() || runningFromWAR()) {
      /*result.add("\n\nRunning from either Jar or War!!\n\n");*/
      return getCurrentJARDirectory();
    } else {
      /*result.add("\n\nRunning from IDE!!\n\n");*/
      return getCurrentProjectDirectory();
    }
  }

  private static String getCurrentProjectDirectory() {
    return new File("").getAbsolutePath();
  }

  private static String getCurrentJARDirectory() {
    try {
      return new File(ApplicationDirectoryUtils.class.getProtectionDomain().getCodeSource()
          .getLocation().toURI().getPath()).getParent();
    } catch (URISyntaxException exception) {
      exception.printStackTrace();
    }
    return null;
  }
}
