package com.teammerge.manager;

import java.io.File;
import java.util.Date;

import com.teammerge.model.RepositoryModel;


public interface IManager {

  /**
   * Start the manager.
   *
   * @return the manager
   * @since 1.4.0
   */
  IManager start();

  /**
   * Stop the manager.
   *
   * @return the manager
   * @since 1.4.0
   */
  IManager stop();

}
