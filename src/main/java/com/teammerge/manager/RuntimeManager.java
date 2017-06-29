package com.teammerge.manager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.sun.jersey.spi.resource.Singleton;
import com.teammerge.Constants;
import com.teammerge.IStoredSettings;
import com.teammerge.Keys;
import com.teammerge.model.ServerSettings;
import com.teammerge.model.ServerStatus;
import com.teammerge.model.SettingModel;
import com.teammerge.utils.StringUtils;
import com.teammerge.utils.XssFilter;

@Singleton
public class RuntimeManager implements IRuntimeManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final IStoredSettings settings;

  private final XssFilter xssFilter;

  private final ServerStatus serverStatus;

  private final ServerSettings settingsModel;

  private File baseFolder;

  private TimeZone timezone;

  @Inject
  private Injector injector;

  @Inject
  public RuntimeManager(IStoredSettings settings, XssFilter xssFilter) {
    this(settings, xssFilter, null);
  }

  public RuntimeManager(IStoredSettings settings, XssFilter xssFilter, File baseFolder) {
    this.settings = settings;
    this.settingsModel = new ServerSettings();
    this.serverStatus = new ServerStatus();
    this.xssFilter = xssFilter;
    this.baseFolder = baseFolder == null ? new File("") : baseFolder;
  }

  public RuntimeManager start() {
    logger.info("Basefolder  : " + baseFolder.getAbsolutePath());
    logger.info("Settings    : " + settings.toString());
    logTimezone("JVM timezone: ", TimeZone.getDefault());
    logTimezone("App timezone: ", getTimezone());
    logger.info("JVM locale  : " + Locale.getDefault());
    logger.info("App locale  : " + (getLocale() == null ? "<client>" : getLocale()));
    return this;
  }

  public RuntimeManager stop() {
    return this;
  }

  public Injector getInjector() {
    return injector;
  }

  public File getBaseFolder() {
    return baseFolder;
  }

  public void setBaseFolder(File folder) {
    this.baseFolder = folder;
  }

  /**
   * Returns the boot date of the GitWebIntegration server.
   *
   * @return the boot date of GitWebIntegration
   */
  public Date getBootDate() {
    return serverStatus.bootDate;
  }

  public ServerSettings getSettingsModel() {
    // ensure that the current values are updated in the setting models
    for (String key : settings.getAllKeys(null)) {
      SettingModel setting = settingsModel.get(key);
      if (setting == null) {
        // unreferenced setting, create a setting model
        setting = new SettingModel();
        setting.name = key;
        settingsModel.add(setting);
      }
      setting.currentValue = settings.getString(key, "");
    }
    // settingsModel.pushScripts = getAllScripts();
    return settingsModel;
  }

  /**
   * Returns the preferred timezone for the GitWebIntegration instance.
   *
   * @return a timezone
   */

  public TimeZone getTimezone() {
    if (timezone == null) {
      String tzid = settings.getString(Keys.web.timezone, null);
      if (StringUtils.isEmpty(tzid)) {
        timezone = TimeZone.getDefault();
        return timezone;
      }
      timezone = TimeZone.getTimeZone(tzid);
    }
    return timezone;
  }

  private void logTimezone(String type, TimeZone zone) {
    SimpleDateFormat df = new SimpleDateFormat("z Z");
    df.setTimeZone(zone);
    String offset = df.format(new Date());
    logger.info("{}{} ({})", new Object[] {type, zone.getID(), offset});
  }


  public Locale getLocale() {
    String lc = settings.getString(Keys.web.forceDefaultLocale, null);
    if (!StringUtils.isEmpty(lc)) {
      int underscore = lc.indexOf('_');
      if (underscore > 0) {
        String lang = lc.substring(0, underscore);
        String cc = lc.substring(underscore + 1);
        return new Locale(lang, cc);
      } else {
        return new Locale(lc);
      }
    }
    return null;
  }

  /**
   * Is GitWebIntegration running in debug mode?
   *
   * @return true if GitWebIntegration is running in debug mode
   */

  public boolean isDebugMode() {
    return settings.getBoolean(Keys.web.debugMode, false);
  }

  /**
   * Returns the file object for the specified configuration key.
   *
   * @return the file
   */

  public File getFileOrFolder(String key, String defaultFileOrFolder) {
    String fileOrFolder = settings.getString(key, defaultFileOrFolder);
    return getFileOrFolder(fileOrFolder);
  }

  /**
   * Returns the file object which may have it's base-path determined by environment variables for
   * running on a cloud hosting service. All GitWebIntegration file or folder retrievals are (at least
   * initially) funneled through this method so it is the correct point to globally override/alter
   * filesystem access based on environment or some other indicator.
   *
   * @return the file
   */

  public File getFileOrFolder(String fileOrFolder) {
    return com.teammerge.utils.FileUtils.resolveParameter(Constants.baseFolder$, baseFolder,
        fileOrFolder);
  }

  /**
   * Returns the runtime settings.
   *
   * @return runtime settings
   */

  public IStoredSettings getSettings() {
    return settings;
  }

  /**
   * Updates the runtime settings.
   *
   * @param settings
   * @return true if the update succeeded
   */

  public boolean updateSettings(Map<String, String> updatedSettings) {
    return settings.saveSettings(updatedSettings);
  }


  public ServerStatus getStatus() {
    // update heap memory status
    serverStatus.heapAllocated = Runtime.getRuntime().totalMemory();
    serverStatus.heapFree = Runtime.getRuntime().freeMemory();
    return serverStatus;
  }

  /**
   * Returns the XSS filter.
   *
   * @return the XSS filter
   */
  public XssFilter getXssFilter() {
    return xssFilter;
  }

}
