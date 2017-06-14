package com.teammerge.manager;

import com.teammerge.services.IUserService;


public interface IUserManager extends IManager, IUserService {

	/**
	 * Returns true if the username represents an internal account
	 *
	 * @param username
	 * @return true if the specified username represents an internal account
 	 * @since 1.4.0
	 */
	boolean isInternalAccount(String username);

}
