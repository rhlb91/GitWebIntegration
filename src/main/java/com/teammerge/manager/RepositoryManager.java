package com.teammerge.manager;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.teammerge.IStoredSettings;
import com.teammerge.Keys;
import com.teammerge.model.ForkModel;
import com.teammerge.model.Metric;
import com.teammerge.model.RegistrantAccessPermission;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.UserModel;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.StringUtils;

public class RepositoryManager implements IRepositoryManager {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final IStoredSettings settings;
	private File repositoriesFolder;
	private final IRuntimeManager runtimeManager;
	private final IUserManager userManager;

	@Inject
	public RepositoryManager(IRuntimeManager runtimeManager,
			IUserManager userManager) {

		this.settings = runtimeManager.getSettings();
		this.runtimeManager = runtimeManager;
		this.userManager = userManager;
	}

	public List<String> getRepositoryList() {
		List<String> repositories =null;
		if (!isValidRepositoryList()) {

			repositoriesFolder = runtimeManager.getFileOrFolder(
					Keys.git.repositoriesFolder, "${baseFolder}/git");
			logger.info("Repositories folder : {}",
					repositoriesFolder.getAbsolutePath());

			// we are not caching OR we have not yet cached OR the cached list
			// is invalid
			long startTime = System.currentTimeMillis();
			 repositories = JGitUtils.getRepositoryList(
					repositoriesFolder, settings.getBoolean(
							Keys.git.onlyAccessBareRepositories, false),
					settings.getBoolean(Keys.git.searchRepositoriesSubfolders,
							true), settings.getInteger(
							Keys.git.searchRecursionDepth, -1), settings
							.getStrings(Keys.git.searchExclusions));

			if (!settings.getBoolean(Keys.git.cacheRepositoryList, false)) {
				// we are not caching
				StringUtils.sortRepositorynames(repositories);
				return repositories;
			} else {
				// we are caching this list
				String msg = "{0} repositories identified in {1} msecs";
				if (settings.getBoolean(Keys.web.showRepositorySizes, true)) {
					// optionally (re)calculate repository sizes
					msg = "{0} repositories identified with calculated folder sizes in {1} msecs";
				}

				for (String repository : repositories) {
					getRepositoryModel(repository);
				}

				// rebuild fork networks
				/*for (RepositoryModel model : repositoryListCache.values()) {
					if (!StringUtils.isEmpty(model.originRepository)) {
						String originKey = getRepositoryKey(model.originRepository);
						if (repositoryListCache.containsKey(originKey)) {
							RepositoryModel origin = repositoryListCache
									.get(originKey);
							origin.addFork(model.name);
						}
					}
				}*/

				long duration = System.currentTimeMillis() - startTime;
				logger.info(MessageFormat.format(msg,
						repositories.size(), duration));
			}
		}

		// return sorted copy of cached list
		/*List<String> list = new ArrayList<String>();
		for (RepositoryModel model : repositoryListCache.values()) {
			list.add(model.name);
		}
		StringUtils.sortRepositorynames(list);*/
		return repositories;
	}

	/**
	 * Returns the cache key for the repository name.
	 *
	 * @param repositoryName
	 * @return the cache key for the repository
	 */
	private String getRepositoryKey(String repositoryName) {
		String name = fixRepositoryName(repositoryName);
		return StringUtils.stripDotGit(name).toLowerCase();
	}
	
	/**
	 * Replaces illegal character patterns in a repository name.
	 *
	 * @param repositoryName
	 * @return a corrected name
	 */
	private String fixRepositoryName(String repositoryName) {
		if (StringUtils.isEmpty(repositoryName)) {
			return repositoryName;
		}

		// Decode url-encoded repository name (issue-278)
		// http://stackoverflow.com/questions/17183110
		String name  = repositoryName.replace("%7E", "~").replace("%7e", "~");
		name = name.replace("%2F", "/").replace("%2f", "/");

		if (name.charAt(name.length() - 1) == '/') {
			name = name.substring(0, name.length() - 1);
		}

		// strip duplicate-slashes from requests for repositoryName (ticket-117, issue-454)
		// specify first char as slash so we strip leading slashes
		char lastChar = '/';
		StringBuilder sb = new StringBuilder();
		for (char c : name.toCharArray()) {
			if (c == '/' && lastChar == c) {
				continue;
			}
			sb.append(c);
			lastChar = c;
		}

		return sb.toString();
	}

	
	/**
	 * Determines if this server has the requested repository.
	 *
	 * @param n
	 * @return true if the repository exists
	 */
	@Override
	public boolean hasRepository(String repositoryName) {
		return hasRepository(repositoryName, false);
	}

	/**
	 * Determines if this server has the requested repository.
	 *
	 * @param n
	 * @param caseInsensitive
	 * @return true if the repository exists
	 */
	@Override
	public boolean hasRepository(String repositoryName,
			boolean caseSensitiveCheck) {
		/*
		 * if (!caseSensitiveCheck &&
		 * settings.getBoolean(Keys.git.cacheRepositoryList, true)) { // if we
		 * are caching use the cache to determine availability // otherwise we
		 * end up adding a phantom repository to the cache String key =
		 * getRepositoryKey(repositoryName); return
		 * repositoryListCache.containsKey(key); }
		 */
		Repository r = getRepository(repositoryName, false);
		if (r == null) {
			return false;
		}
		r.close();
		return true;
	}

	/**
	 * Compare the last repository list setting checksum to the current
	 * checksum. If different then clear the cache so that it may be rebuilt.
	 *
	 * @return true if the cached repository list is valid since the last check
	 */
	private boolean isValidRepositoryList() {
		return false;
	}

	@Override
	public IManager start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IManager stop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getRepositoriesFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getGrapesFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastActivityDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrantAccessPermission> getUserAccessPermissions(
			UserModel user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrantAccessPermission> getUserAccessPermissions(
			RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setUserAccessPermissions(RepositoryModel repository,
			Collection<RegistrantAccessPermission> permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getRepositoryUsers(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistrantAccessPermission> getTeamAccessPermissions(
			RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setTeamAccessPermissions(RepositoryModel repository,
			Collection<RegistrantAccessPermission> permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getRepositoryTeams(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToCachedRepositoryList(RepositoryModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetRepositoryListCache() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetRepositoryCache(String repositoryName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Repository getRepository(String repositoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Repository getRepository(String repositoryName, boolean logError) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RepositoryModel> getRepositoryModels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RepositoryModel> getRepositoryModels(UserModel user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RepositoryModel getRepositoryModel(UserModel user,
			String repositoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RepositoryModel getRepositoryModel(String repositoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getStarCount(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasFork(String username, String origin) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFork(String username, String origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForkModel getForkNetwork(String repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long updateLastChangeFields(Repository r, RepositoryModel model) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Metric> getRepositoryDefaultMetrics(RepositoryModel model,
			Repository repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateConfiguration(Repository r, RepositoryModel repository) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean deleteRepositoryModel(RepositoryModel model) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRepository(String repositoryName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getAllScripts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPreReceiveScriptsInherited(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPreReceiveScriptsUnused(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPostReceiveScriptsInherited(
			RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getPostReceiveScriptsUnused(RepositoryModel repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCollectingGarbage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCollectingGarbage(String repositoryName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(String repository) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isIdle(Repository repository) {
		// TODO Auto-generated method stub
		return false;
	}
}
