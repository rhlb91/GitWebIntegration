package com.teammerge.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.teammerge.Constants;

/**
 * ServerStatus encapsulates runtime status information about the server including some information
 * about the system environment.
 *
 *
 */
public class ServerStatus implements Serializable {

  private static final long        serialVersionUID = 1L;

  public final Date                bootDate;

  public final String              version;

  public final String              releaseDate;

  public final Map<String, String> systemProperties;

  public final long                heapMaximum;

  public volatile long             heapAllocated;

  public volatile long             heapFree;

  public boolean                   isGO;

  public String                    servletContainer;

  public ServerStatus() {
    this.bootDate = new Date();
    this.version = Constants.getVersion();
    this.releaseDate = Constants.getBuildDate();

    this.heapMaximum = Runtime.getRuntime().maxMemory();

    this.systemProperties = new TreeMap<String, String>();
    put("file.encoding");
    put("java.home");
    put("java.awt.headless");
    put("java.io.tmpdir");
    put("java.runtime.name");
    put("java.runtime.version");
    put("java.vendor");
    put("java.version");
    put("java.vm.info");
    put("java.vm.name");
    put("java.vm.vendor");
    put("java.vm.version");
    put("os.arch");
    put("os.name");
    put("os.version");
  }

  private void put(String key) {
    systemProperties.put(key, System.getProperty(key));
  }
}
