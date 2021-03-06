package com.teammerge.manager;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.google.inject.Injector;
import com.teammerge.IStoredSettings;
import com.teammerge.model.ServerSettings;
import com.teammerge.model.ServerStatus;

public interface IRuntimeManager extends IManager {

  Injector getInjector();

  void setBaseFolder(File folder);

  File getBaseFolder();

  /**
   * Returns the preferred timezone for the Gitblit instance.
   *
   * @return a timezone
   * @since 1.4.0
   */
  TimeZone getTimezone();

  /**
   * Returns the fixed locale for clients, or null if clients may choose their locale
   *
   * @return a fixed locale or null if clients are allowed to specify locale preference
   * @since 1.5.1
   */
  Locale getLocale();

  /**
   * Determine if this Gitblit instance is running in debug mode
   *
   * @return true if Gitblit is running in debug mode
   * @since 1.4.0
   */
  boolean isDebugMode();

  /**
   * Returns the boot date of the Gitblit server.
   *
   * @return the boot date of Gitblit
   * @since 1.4.0
   */
  Date getBootDate();

  /**
   * Returns the server status.
   *
   * @return the server status
   * @since 1.4.0
   */
  ServerStatus getStatus();

  /**
   * Returns the descriptions/comments of the Gitblit config settings.
   *
   * @return SettingsModel
   * @since 1.4.0
   */
  ServerSettings getSettingsModel();

  /**
   * Returns the file object for the specified configuration key.
   *
   * @return the file
   * @since 1.4.0
   */
  File getFileOrFolder(String key, String defaultFileOrFolder);

  /**
   * Returns the file object which may have it's base-path determined by environment variables for
   * running on a cloud hosting service. All Gitblit file or folder retrievals are (at least
   * initially) funneled through this method so it is the correct point to globally override/alter
   * filesystem access based on environment or some other indicator.
   *
   * @return the file
   * @since 1.4.0
   */
  File getFileOrFolder(String fileOrFolder);

  /**
   * Returns the runtime settings.
   *
   * @return settings
   * @since 1.4.0
   */
  IStoredSettings getSettings();

  /**
   * Updates the runtime settings.
   *
   * @param settings
   * @return true if the update succeeded
   * @since 1.4.0
   */
  boolean updateSettings(Map<String, String> updatedSettings);

  /**
   * Returns the HTML sanitizer used to clean user content.
   *
   * @return the HTML sanitizer
   */
  // XssFilter getXssFilter();
}
