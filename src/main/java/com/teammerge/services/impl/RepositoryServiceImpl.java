package com.teammerge.services.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.Constants.AccessRestrictionType;
import com.teammerge.Constants.AuthorizationControl;
import com.teammerge.Constants.CloneStatus;
import com.teammerge.Constants.CommitMessageRenderer;
import com.teammerge.Constants.MergeType;
import com.teammerge.IStoredSettings;
import com.teammerge.Keys;
import com.teammerge.cache.CommitCache;
import com.teammerge.cache.ObjectCache;
import com.teammerge.dao.BaseDao;
import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.dao.RepositoryDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;
import com.teammerge.form.CommitTreeRequestForm;
import com.teammerge.manager.IManager;
import com.teammerge.model.CreateBranchOptions;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.ForkModel;
import com.teammerge.model.Metric;
import com.teammerge.model.PathModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RegistrantAccessPermission;
import com.teammerge.model.RepoCloneStatusModel;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.UserModel;
import com.teammerge.services.CompanyService;
import com.teammerge.services.GitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.strategy.BlobConversionStrategy;
import com.teammerge.strategy.CloneStrategy;
import com.teammerge.utils.ApplicationDirectoryUtils;
import com.teammerge.utils.ArrayUtils;
import com.teammerge.utils.ByteFormat;
import com.teammerge.utils.DeepCopier;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.JGitUtils.LastChange;
import com.teammerge.utils.LoggerUtils;
import com.teammerge.utils.StringUtils;

@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {
  private final static Logger LOG = LoggerFactory.getLogger(RepositoryServiceImpl.class);

  private final Map<String, RepositoryModel> repositoryListCache =
      new ConcurrentHashMap<String, RepositoryModel>();

  private final ObjectCache<Long> repositorySizeCache = new ObjectCache<Long>();

  private final ObjectCache<List<Metric>> repositoryMetricsCache = new ObjectCache<List<Metric>>();

  @Value("${git.repository.folderName}")
  private String repoFolderName;

  @Resource(name = "cloneStrategy")
  private CloneStrategy cloneStrategy;

  @Resource(name = "blobConversionStrategy")
  BlobConversionStrategy blobStrategy;

  @Value("${app.debug}")
  private String debug;

  @Autowired
  private RuntimeServiceImpl runtimeService;

  @Resource(name = "gitService")
  private GitService gitService;

  private RepositoryDao repositoryDao;

  @Resource(name = "companyService")
  private CompanyService companyService;

  @Resource(name = "repoCredentialDao")
  private RepoCredentialDao repoCredentialDao;

  BaseDao<RepoCloneStatusModel> repoCloneStatusDao;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  @Value("${git.repository.folderName}")
  public String getRepositoriesFolderPath(String gitFolder) {
    return "${git.baseFolder}" + File.pathSeparator + repoFolderName;
  }

  // TODO : change this logic of checking clone status - RB
  public List<RepositoryModel> getRepositoryModels() {
    long methodStart = System.currentTimeMillis();
    List<RepositoryModel> repositories = new ArrayList<RepositoryModel>();
    List<String> list = getRepositoryListFromDB();

    if (CollectionUtils.isEmpty(list)) {
      LOG.warn("No repositories found in DB!!");
      return null;
    }

    List<RepoCloneStatusModel> repoStatusModels = repoCloneStatusDao.fetchAll();
    RepoCloneStatusModel repoStatusModel = null;
    for (String repoName : list) {

      if (CollectionUtils.isEmpty(repoStatusModels)) {
        LOG.error("No Repo status entry found in DB for repo " + repoName);
        return null;
      }

      for (RepoCloneStatusModel repoStatusModelItem : repoStatusModels) {
        if (repoName.equals(repoStatusModelItem.getRepoName())) {
          repoStatusModel = repoStatusModelItem;
          break;
        }
      }

      if (repoStatusModel != null
          && CloneStatus.forName(repoStatusModel.getCloneStatus()).equals(CloneStatus.COMPLETED)) {
        RepositoryModel model = getRepositoryModel(repoName);
        if (model != null) {
          repositories.add(model);
        }
      }
    }
    LOG.info(MessageFormat.format("{0} repository models loaded in {1}", repositories.size(),
        LoggerUtils.getTimeInSecs(methodStart, System.currentTimeMillis())));
    return repositories;
  }

  public Repository getRepository(String repositoryName, boolean updated) {
    Repository repo = getUpdatedRepository(repositoryName, updated);

    // this is because, might be the repo is still in cloning phase,thus using a cached instance if
    // possible
    if (repo == null) {
      LOG.warn("Updated repo returns 'null', Loading repo " + repositoryName);
      repo = getRepository(repositoryName);
    }

    if (repo == null) {
      LOG.error("\nCannot Load Repository" + " " + repositoryName);
      return null;
    }
    return repo;
  }

  private Repository loadRepository(String name, boolean logError) {
    String repositoryName = fixRepositoryName(name);

    if (isCollectingGarbage(repositoryName)) {
      LOG.warn(MessageFormat.format("Rejecting request for {0}, busy collecting garbage!",
          repositoryName));
      return null;
    }

    File dir = FileKey.resolve(new File(getRepositoriesFolder(), repositoryName), FS.DETECTED);
    if (dir == null)
      return null;

    Repository r = null;
    try {
      FileKey key = FileKey.exact(dir, FS.DETECTED);
      r = RepositoryCache.open(key, true);
    } catch (IOException e) {
      if (logError) {
        LOG.error(
            "Failed to find " + new File(getRepositoriesFolder(), repositoryName).getAbsolutePath(),
            e);
      }
    }
    return r;
  }

  /**
   * This function gets the repository url from the configuration file and tries to find if the
   * directory already exists or not. <br>
   * <br>
   * If the repository does not exists in the directory , this function will create the repository
   * from the remote repository defined in the config.properties <br>
   * <br>
   * If the repository exists, this function tries to update the repository by taking a pull of the
   * remote repository
   * 
   * <br>
   * <br>
   * This function takes first argument as repository name to update or create (if not exists), if
   * specified null it will try to update all the repositories mentioned in the config file
   * 
   * @return
   */
  private Repository getUpdatedRepository(String repoName, boolean updateRequired) {
    Repository repo = null;
    boolean toUpdate = false;

    File repositoriesFolder = getRepositoriesFolder();
    if (!repositoriesFolder.exists() || !repositoriesFolder.isDirectory()) {
      boolean isDirCreated = repositoriesFolder.mkdir();

      if (isDirCreated) {
        toUpdate = true;
      } else {
        LOG.error("Cannot create directory " + repositoriesFolder.getAbsolutePath()
            + ", resolve the issue create and clone dir!! ");
      }
    }

    boolean isRepoExists = isRepoExists(repositoriesFolder, repoName);
    if (toUpdate || updateRequired || !isRepoExists) {
      repo = cloneStrategy.createOrUpdateRepo(repositoriesFolder, repoName, isRepoExists);
    }
    return repo;
  }

  boolean isRepoExists(File repoFolder, String repoName) {
    boolean isRepoExists = false;

    if (repoFolder == null || StringUtils.isEmpty(repoName)) {
      return false;
    }

    if (repoFolder.list().length > 0) {
      for (String fName : repoFolder.list()) {
        if (repoName.equals(fName)) {
          isRepoExists = true;
          break;
        }
      }
    }
    return isRepoExists;
  }

  /**
   * No need to update repository from remote, as it is only printing the list of repositories
   * Available in local <br>
   * <br>
   * This should be used only with RestController V1
   */
  public List<String> getRepositoryList() {
    List<String> repositories = null;
    if (repositoryListCache.size() == 0 || !isValidRepositoryList()) {

      // we are not caching OR we have not yet cached OR the cached list
      // is invalid
      long startTime = System.currentTimeMillis();

      repositories =
          JGitUtils.getRepositoryList(getRepositoriesFolder(),
              getSettings().getBoolean(Keys.git.onlyAccessBareRepositories, false), getSettings()
                  .getBoolean(Keys.git.searchRepositoriesSubfolders, true), getSettings()
                  .getInteger(Keys.git.searchRecursionDepth, -1),
              getSettings().getStrings(Keys.git.searchExclusions));

      if (!getSettings().getBoolean(Keys.git.cacheRepositoryList, true)) {
        // we are not caching
        StringUtils.sortRepositorynames(repositories);
        return repositories;
      } else {
        // we are caching this list
        String msg = "{0} repositories identified in {1}";
        if (getSettings().getBoolean(Keys.web.showRepositorySizes, true)) {
          // optionally (re)calculate repository sizes
          msg = "{0} repositories identified with calculated folder sizes in {1}";
        }

        for (String repository : repositories) {
          getRepositoryModel(repository);
        }

        // rebuild fork networks
        for (RepositoryModel model : repositoryListCache.values()) {
          if (!StringUtils.isEmpty(model.getOriginRepository())) {
            String originKey = getRepositoryKey(model.getOriginRepository());
            if (repositoryListCache.containsKey(originKey)) {
              RepositoryModel origin = repositoryListCache.get(originKey);
              origin.addFork(model.getName());
            }
          }
        }

        LOG.info(MessageFormat.format(msg, repositories.size(),
            LoggerUtils.getTimeInSecs(startTime, System.currentTimeMillis())));
      }
    }

    // return sorted copy of cached list
    List<String> list = new ArrayList<String>();
    for (RepositoryModel model : repositoryListCache.values()) {
      Repository r = getRepository(model.getName());
      if (r == null) {
        // repository is missing
        removeFromCachedRepositoryList(model.getName());
        LOG.warn(MessageFormat.format("Repository \"{0}\" is missing! Removing from cache.",
            model.getName()));
        continue;
      }
      list.add(model.getName());
    }

    StringUtils.sortRepositorynames(list);

    return list;
  }

  /**
   * This function is responsible for fetching repositories list from DB. This function is better to
   * use with Rest Controller version V2
   * 
   * @return
   */
  public List<String> getRepositoryListFromDB() {
    List<String> repositories = null;

    if (repositoryListCache.size() == 0 || !isValidRepositoryList()) {
      // we are not caching OR we have not yet cached OR the cached list
      // is invalid

      long startTime = System.currentTimeMillis();
      repositories = repositoryDao.fetchAllRepositoryNames();

      if (CollectionUtils.isEmpty(repositories)) {
        return null;
      }

      if (!getSettings().getBoolean(Keys.git.cacheRepositoryList, true)) {
        // we are not caching
        StringUtils.sortRepositorynames(repositories);
        return repositories;
      } else {
        // we are caching this list
        String msg = "{0} repositories identified in {1}";
        if (getSettings().getBoolean(Keys.web.showRepositorySizes, true)) {
          // optionally (re)calculate repository sizes
          msg = "{0} repositories identified with calculated folder sizes in {1}";
        }

        for (String repository : repositories) {
          getRepositoryModel(repository);
        }

        // rebuild fork networks
        for (RepositoryModel model : repositoryListCache.values()) {
          if (!StringUtils.isEmpty(model.getOriginRepository())) {
            String originKey = getRepositoryKey(model.getOriginRepository());
            if (repositoryListCache.containsKey(originKey)) {
              RepositoryModel origin = repositoryListCache.get(originKey);
              origin.addFork(model.getName());
            }
          }
        }

        LOG.info(MessageFormat.format(msg, repositories.size(),
            LoggerUtils.getTimeInSecs(startTime, System.currentTimeMillis())));
      }
    }

    // return sorted copy of cached list
    List<String> list = new ArrayList<String>();
    for (RepositoryModel model : repositoryListCache.values()) {
      Repository r = getRepository(model.getName());
      if (r == null) {
        // repository is missing
        removeFromCachedRepositoryList(model.getName());
        LOG.warn(MessageFormat.format("Repository \"{0}\" is missing! Removing from cache.",
            model.getName()));
        continue;
      }
      list.add(model.getName());
    }

    StringUtils.sortRepositorynames(list);

    return list;
  }

  @Override
  public IManager start() {
    return null;
  }

  @Override
  public IManager stop() {
    return null;
  }

  /**
   * Eg- git , where all the repositories will be stored
   */
  @Override
  public File getRepositoriesFolder() {

    File repoFolder =
        runtimeService.getRuntimeManager().getFileOrFolder(Keys.git.repositoriesFolder,
            ApplicationDirectoryUtils.getProgramDirectory() + "/" + repoFolderName);

    if (!repoFolder.exists()) {
      boolean isDirCreated = repoFolder.mkdir();

      if (!isDirCreated) {
        LOG.error("Cannot create repository folder " + repoFolderName + "at location: "
            + ApplicationDirectoryUtils.getProgramDirectory() + "/"
            + ". Try creating it mannually!!");
      }
    }
    return repoFolder;
  }

  @Override
  public File getGrapesFolder() {
    return null;
  }

  @Override
  public Date getLastActivityDate() {
    return null;
  }

  @Override
  public List<RegistrantAccessPermission> getUserAccessPermissions(UserModel user) {
    return null;
  }

  @Override
  public List<RegistrantAccessPermission> getUserAccessPermissions(RepositoryModel repository) {
    return null;
  }

  @Override
  public boolean setUserAccessPermissions(RepositoryModel repository,
      Collection<RegistrantAccessPermission> permissions) {
    return false;
  }

  @Override
  public List<String> getRepositoryUsers(RepositoryModel repository) {
    return null;
  }

  @Override
  public List<RegistrantAccessPermission> getTeamAccessPermissions(RepositoryModel repository) {
    return null;
  }

  @Override
  public boolean setTeamAccessPermissions(RepositoryModel repository,
      Collection<RegistrantAccessPermission> permissions) {
    return false;
  }

  @Override
  public List<String> getRepositoryTeams(RepositoryModel repository) {
    return null;
  }

  @Override
  public void addToCachedRepositoryList(RepositoryModel model) {
    if (getSettings().getBoolean(Keys.git.cacheRepositoryList, true)) {
      String key = getRepositoryKey(model.getName());
      repositoryListCache.put(key, model);

      // update the fork origin repository with this repository clone
      if (!StringUtils.isEmpty(model.getOriginRepository())) {
        String originKey = getRepositoryKey(model.getOriginRepository());
        if (repositoryListCache.containsKey(originKey)) {
          RepositoryModel origin = repositoryListCache.get(originKey);
          origin.addFork(model.getName());
        }
      }
    }

  }

  private IStoredSettings getSettings() {
    return runtimeService.getRuntimeManager().getSettings();
  }

  @Override
  public void resetRepositoryListCache() {
    LOG.info("Repository cache manually reset");
    repositoryListCache.clear();
    repositorySizeCache.clear();
    repositoryMetricsCache.clear();
    CommitCache.instance().clear();

  }

  @Override
  public void resetRepositoryCache(String repositoryName) {
    removeFromCachedRepositoryList(repositoryName);
    clearRepositoryMetadataCache(repositoryName);
    // force a reload of the repository data (ticket-82, issue-433)
    getRepositoryModel(repositoryName);

  }

  /**
   * Clears all the cached metadata for the specified repository.
   *
   * @param repositoryName
   */
  private void clearRepositoryMetadataCache(String repositoryName) {
    repositorySizeCache.remove(repositoryName);
    repositoryMetricsCache.remove(repositoryName);
    CommitCache.instance().clear(repositoryName);
  }

  @Override
  public Repository getRepository(String repositoryName) {
    return loadRepository(repositoryName, true);
  }

  @Override
  public List<RepositoryModel> getRepositoryModels(UserModel user) {
    long methodStart = System.currentTimeMillis();
    List<String> list = getRepositoryListFromDB();
    List<RepositoryModel> repositories = new ArrayList<RepositoryModel>();
    for (String repo : list) {
      RepositoryModel model = getRepositoryModel(repo);
      if (model != null) {
        repositories.add(model);
      }
    }
    long duration = System.currentTimeMillis() - methodStart;
    LOG.info(MessageFormat.format("{0} repository models loaded in {1} msecs", duration));
    return repositories;
  }

  @Override
  public RepositoryModel getRepositoryModel(UserModel user, String repositoryName) {
    return null;
  }

  @Override
  public RepositoryModel getRepositoryModel(String name) {
    String repositoryName = fixRepositoryName(name);

    String repositoryKey = getRepositoryKey(repositoryName);
    if (!repositoryListCache.containsKey(repositoryKey)) {
      RepositoryModel model = loadRepositoryModel(repositoryName);
      if (model == null) {
        return null;
      }
      addToCachedRepositoryList(model);
      return DeepCopier.copy(model);
    }
    // cached model
    RepositoryModel model = repositoryListCache.get(repositoryKey);

    if (isCollectingGarbage(model.getName())) {
      // Gitblit is busy collecting garbage, use our cached model
      RepositoryModel rm = DeepCopier.copy(model);
      rm.setCollectingGarbage(true);
      return rm;
    }

    // check for updates
    Repository r = getRepository(model.getName());
    if (r == null) {
      // repository is missing
      removeFromCachedRepositoryList(repositoryName);
      LOG.error(MessageFormat.format("Repository \"{0}\" is missing! Removing from cache.",
          repositoryName));
      return null;
    }

    FileBasedConfig config = (FileBasedConfig) getRepositoryConfig(r);
    if (config.isOutdated()) {
      // reload model
      LOG.debug(MessageFormat.format(
          "Config for \"{0}\" has changed. Reloading model and updating cache.", repositoryName));
      model = loadRepositoryModel(model.getName());
      removeFromCachedRepositoryList(model.getName());
      addToCachedRepositoryList(model);
    } else {
      // update a few repository parameters
      if (!model.isHasCommits()) {
        // update hasCommits, assume a repository only gains commits :)
        model.setHasCommits(JGitUtils.hasCommits(r));
      }

      updateLastChangeFields(r, model);
    }
    r.close();

    // return a copy of the cached model
    return DeepCopier.copy(model);
  }

  @Override
  public long getStarCount(RepositoryModel repository) {
    return 0;
  }

  @Override
  public boolean hasRepository(String repositoryName) {
    return hasRepository(repositoryName, false);
  }

  @Override
  public boolean hasRepository(String repositoryName, boolean caseSensitiveCheck) {

    if (!caseSensitiveCheck && getSettings().getBoolean(Keys.git.cacheRepositoryList, true)) {
      // if we are caching use the cache to determine availability
      // otherwise we end up adding a phantom repository to the cache
      String key = getRepositoryKey(repositoryName);
      return repositoryListCache.containsKey(key);
    }

    Repository r = loadRepository(repositoryName, false);
    if (r == null) {
      return false;
    }
    r.close();
    return true;
  }

  @Override
  public boolean hasFork(String username, String origin) {
    return false;
  }

  @Override
  public String getFork(String username, String origin) {
    return null;
  }

  @Override
  public ForkModel getForkNetwork(String repository) {
    return null;
  }

  @Override
  public long updateLastChangeFields(Repository r, RepositoryModel model) {
    LastChange lc = JGitUtils.getLastChange(r);
    model.setLastChange(lc.when);
    model.setLastChangeAuthor(lc.who);

    if (!getSettings().getBoolean(Keys.web.showRepositorySizes, true)
        || model.isSkipSizeCalculation()) {
      model.setSize(null);
      return 0L;
    }
    if (!repositorySizeCache.hasCurrent(model.getName(), model.getLastChange())) {
      File gitDir = r.getDirectory();
      long sz = com.teammerge.utils.FileUtils.folderSize(gitDir);
      repositorySizeCache.updateObject(model.getName(), model.getLastChange(), sz);
    }
    long size = repositorySizeCache.getObject(model.getName());
    ByteFormat byteFormat = new ByteFormat();
    model.setSize(byteFormat.format(size));
    return size;
  }

  @Override
  public List<Metric> getRepositoryDefaultMetrics(RepositoryModel model, Repository repository) {
    return null;
  }

  @Override
  public void updateConfiguration(Repository r, RepositoryModel repository) {

  }

  @Override
  public boolean deleteRepositoryModel(RepositoryModel model) {
    return false;
  }

  @Override
  public boolean deleteRepository(String repositoryName) {
    return false;
  }

  @Override
  public List<String> getAllScripts() {
    return null;
  }

  @Override
  public List<String> getPreReceiveScriptsInherited(RepositoryModel repository) {
    return null;
  }

  @Override
  public List<String> getPreReceiveScriptsUnused(RepositoryModel repository) {
    return null;
  }

  @Override
  public List<String> getPostReceiveScriptsInherited(RepositoryModel repository) {
    return null;
  }

  @Override
  public List<String> getPostReceiveScriptsUnused(RepositoryModel repository) {
    return null;
  }

  @Override
  public boolean isCollectingGarbage() {
    return false;
  }

  @Override
  public boolean isCollectingGarbage(String repositoryName) {
    return false;
  }

  @Override
  public void closeAll() {

  }

  @Override
  public void close(String repository) {

  }

  @Override
  public boolean isIdle(Repository repository) {
    return false;
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
    String name = repositoryName.replace("%7E", "~").replace("%7e", "~");
    name = name.replace("%2F", "/").replace("%2f", "/");

    if (name.charAt(name.length() - 1) == '/') {
      name = name.substring(0, name.length() - 1);
    }

    // strip duplicate-slashes from requests for repositoryName (ticket-117,
    // issue-454)
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
   * Create a repository model from the configuration and repository data.
   *
   * @param repositoryName
   * @return a repositoryModel or null if the repository does not exist
   */
  private RepositoryModel loadRepositoryModel(String repositoryName) {
    IStoredSettings settings = getSettings();
    Repository r = getRepository(repositoryName, false);
    if (r == null) {
      return null;
    }
    RepositoryModel model = new RepositoryModel();
    model.setBare(r.isBare());
    File basePath = getRepositoriesFolder();
    if (model.isBare()) {
      model.setName(com.teammerge.utils.FileUtils.getRelativePath(basePath, r.getDirectory()));
    } else {
      model.setName(com.teammerge.utils.FileUtils.getRelativePath(basePath, r.getDirectory()
          .getParentFile()));
    }
    if (StringUtils.isEmpty(model.getName())) {
      // Repository is NOT located relative to the base folder because it
      // is symlinked. Use the provided repository name.
      model.setName(repositoryName);
    }
    model.setProjectPath(StringUtils.getFirstPathElement(repositoryName));

    StoredConfig config = r.getConfig();
    boolean hasOrigin = false;

    if (config != null) {
      // Initialize description from description file
      hasOrigin = !StringUtils.isEmpty(config.getString("remote", "origin", "url"));
      if (getConfig(config, "description", null) == null) {
        File descFile = new File(r.getDirectory(), "description");
        if (descFile.exists()) {
          String desc =
              com.teammerge.utils.FileUtils.readContent(descFile,
                  System.getProperty("line.separator"));
          if (!desc.toLowerCase().startsWith("unnamed repository")) {
            config.setString(Constants.CONFIG_GITBLIT, null, "description", desc);
          }
        }
      }
      model.setDescription(getConfig(config, "description", ""));
      model.setOriginRepository(getConfig(config, "originRepository", null));
      model.addOwners(ArrayUtils.fromString(getConfig(config, "owner", "")));
      model.setAcceptNewPatchsets(getConfig(config, "acceptNewPatchsets", true));
      model.setAcceptNewTickets(getConfig(config, "acceptNewTickets", true));
      model.setRequireApproval(getConfig(config, "requireApproval",
          settings.getBoolean(Keys.tickets.requireApproval, false)));
      model.setMergeTo(getConfig(config, "mergeTo", null));
      model.setMergeType(MergeType.fromName(getConfig(config, "mergeType",
          settings.getString(Keys.tickets.mergeType, null))));
      model.setUseIncrementalPushTags(getConfig(config, "useIncrementalPushTags", false));
      model.setIncrementalPushTagPrefix(getConfig(config, "incrementalPushTagPrefix", null));
      model.setAllowForks(getConfig(config, "allowForks", true));
      model.setAccessRestriction(AccessRestrictionType.fromName(getConfig(config,
          "accessRestriction", settings.getString(Keys.git.defaultAccessRestriction, "PUSH"))));
      model.setAuthorizationControl(AuthorizationControl.fromName(getConfig(config,
          "authorizationControl", settings.getString(Keys.git.defaultAuthorizationControl, null))));
      model.setVerifyCommitter(getConfig(config, "verifyCommitter", false));
      model.setShowRemoteBranches(getConfig(config, "showRemoteBranches", hasOrigin));
      model.setFrozen(getConfig(config, "isFrozen", false));
      model.setSkipSizeCalculation(getConfig(config, "skipSizeCalculation", false));
      model.setSkipSummaryMetrics(getConfig(config, "skipSummaryMetrics", false));
      model.setCommitMessageRenderer(CommitMessageRenderer.fromName(getConfig(config,
          "commitMessageRenderer", settings.getString(Keys.web.commitMessageRenderer, null))));
      /*
       * model.federationStrategy = FederationStrategy.fromName(getConfig( config,
       * "federationStrategy", null));
       */
      model.setFederationSets(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "federationSets"))));
      model.setFederated(getConfig(config, "isFederated", false));
      model.setGcThreshold(getConfig(config, "gcThreshold",
          settings.getString(Keys.git.defaultGarbageCollectionThreshold, "500KB")));
      model.setGcPeriod(getConfig(config, "gcPeriod",
          settings.getInteger(Keys.git.defaultGarbageCollectionPeriod, 7)));
      try {
        model.setLastGC(new SimpleDateFormat(Constants.ISO8601).parse(getConfig(config, "lastGC",
            "1970-01-01'T'00:00:00Z")));
      } catch (Exception e) {
        model.setLastGC(new Date(0));
      }
      model.setMaxActivityCommits(getConfig(config, "maxActivityCommits",
          settings.getInteger(Keys.web.maxActivityCommits, 0)));
      model.setOrigin(config.getString("remote", "origin", "url"));
      if (model.getOrigin() != null) {
        model.setOrigin(model.getOrigin().replace('\\', '/'));
        model.setMirror(config.getBoolean("remote", "origin", "mirror", false));
      }
      model.setPreReceiveScripts(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "preReceiveScript"))));
      model.setPostReceiveScripts(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "postReceiveScript"))));
      model.setMailingLists(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "mailingList"))));
      model.setIndexedBranches(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "indexBranch"))));
      model.setMetricAuthorExclusions(new ArrayList<String>(Arrays.asList(config.getStringList(
          Constants.CONFIG_GITBLIT, null, "metricAuthorExclusions"))));

      // Custom defined properties
      model.setCustomFields(new LinkedHashMap<String, String>());
      for (String aProperty : config.getNames(Constants.CONFIG_GITBLIT,
          Constants.CONFIG_CUSTOM_FIELDS)) {
        model.getCustomFields().put(aProperty,
            config.getString(Constants.CONFIG_GITBLIT, Constants.CONFIG_CUSTOM_FIELDS, aProperty));
      }
    }
    model.setHEAD(JGitUtils.getHEADRef(r));
    if (StringUtils.isEmpty(model.getMergeTo())) {
      model.setMergeTo(model.getHEAD());
    }
    model.setAvailableRefs(JGitUtils.getAvailableHeadTargets(r));
    model.setSparkleshareId(JGitUtils.getSparkleshareId(r));
    model.setHasCommits(JGitUtils.hasCommits(r));
    updateLastChangeFields(r, model);
    r.close();

    if (StringUtils.isEmpty(model.getOriginRepository()) && model.getOrigin() != null
        && model.getOrigin().startsWith("file://")) {
      // repository was cloned locally... perhaps as a fork
      try {
        File folder = new File(new URI(model.getOrigin()));
        String originRepo =
            com.teammerge.utils.FileUtils.getRelativePath(getRepositoriesFolder(), folder);
        if (!StringUtils.isEmpty(originRepo)) {
          // ensure origin still exists
          File repoFolder = new File(getRepositoriesFolder(), originRepo);
          if (repoFolder.exists()) {
            model.setOriginRepository(originRepo.toLowerCase());

            // persist the fork origin
            updateConfiguration(r, model);
          }
        }
      } catch (URISyntaxException e) {
        LOG.error("Failed to determine fork for " + model, e);
      }
    }
    return model;
  }

  /**
   * Removes the repository from the list of cached repositories.
   *
   * @param name
   * @return the model being removed
   */
  private RepositoryModel removeFromCachedRepositoryList(String name) {
    if (StringUtils.isEmpty(name)) {
      return null;
    }
    String key = getRepositoryKey(name);
    return repositoryListCache.remove(key);
  }

  /**
   * Workaround JGit. I need to access the raw config object directly in order to see if the config
   * is dirty so that I can reload a repository model. If I use the stock JGit method to get the
   * config it already reloads the config. If the config changes are made within Gitblit this is
   * fine as the returned config will still be flagged as dirty. BUT... if the config is manipulated
   * outside Gitblit then it fails to recognize this as dirty.
   *
   * @param r
   * @return a config
   */
  private StoredConfig getRepositoryConfig(Repository r) {
    try {
      Field f = r.getClass().getDeclaredField("repoConfig");
      f.setAccessible(true);
      StoredConfig config = (StoredConfig) f.get(r);
      return config;
    } catch (Exception e) {
      LOG.error("Failed to retrieve \"repoConfig\" via reflection", e);
    }
    return r.getConfig();
  }

  /**
   * Returns the gitblit string value for the specified key. If key is not set, returns
   * defaultValue.
   *
   * @param config
   * @param field
   * @param defaultValue
   * @return field value or defaultValue
   */
  private String getConfig(StoredConfig config, String field, String defaultValue) {
    String value = config.getString(Constants.CONFIG_GITBLIT, null, field);
    if (StringUtils.isEmpty(value)) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Returns the gitblit boolean value for the specified key. If key is not set, returns
   * defaultValue.
   *
   * @param config
   * @param field
   * @param defaultValue
   * @return field value or defaultValue
   */
  private boolean getConfig(StoredConfig config, String field, boolean defaultValue) {
    return config.getBoolean(Constants.CONFIG_GITBLIT, field, defaultValue);
  }

  /**
   * Returns the gitblit string value for the specified key. If key is not set, returns
   * defaultValue.
   *
   * @param config
   * @param field
   * @param defaultValue
   * @return field value or defaultValue
   */
  private int getConfig(StoredConfig config, String field, int defaultValue) {
    String value = config.getString(Constants.CONFIG_GITBLIT, null, field);
    if (StringUtils.isEmpty(value)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (Exception e) {
    }
    return defaultValue;
  }

  /**
   * Compare the last repository list setting checksum to the current checksum. If different then
   * clear the cache so that it may be rebuilt.
   *
   * @return true if the cached repository list is valid since the last check
   */
  private boolean isValidRepositoryList() {
    return false;
  }

  public Map<String, Object> createBranch(final String companyId, final String projectId,
      final String branchName, final String startingPoint) throws Exception {
    Map<String, Object> result = new HashMap<>();

    // setting default to failure, updating in case of success
    result.put("result", RepositoryService.Result.FAILURE);
    result.put("branch", null);

    Ref branch = null;

    String remoteRepoUrl = companyService.getRemoteUrlForCompanyAndProject(companyId, projectId);

    if (remoteRepoUrl == null) {
      result.put("reason", "Remote url not found with companyId: " + companyId + ", projectId: "
          + projectId);
      return result;
    }

    RepoCredentials repoCreds =
        repoCredentialDao.fetchEntity(new RepoCredentialsKey(companyId, projectId));

    if (repoCreds == null) {
      result.put("reason", "Credentails not found for companyId: " + companyId + ", projectId: "
          + projectId);
      return result;
    }

    CreateBranchOptions branchOptions = new CreateBranchOptions();
    branchOptions.setBranchName(branchName);
    branchOptions.setCompanyName(companyId);
    branchOptions.setRemoteURL(remoteRepoUrl);
    branchOptions.setUserName(repoCreds.getUsername());
    branchOptions.setPassword(repoCreds.getPassword());
    branchOptions.setStartingPoint(startingPoint);

    try (Repository r = getRepository(projectId, false)) {
      branchOptions.setRepo(r);
      branch = gitService.createBranch(branchOptions);

      if (branch != null) {
        result.put("result", RepositoryService.Result.SUCCESS);
        result.put("branch", branch);
        LOG.info("Branch created with name: " + branchName + ", for repo: " + projectId
            + ", with Id: " + branch.getObjectId());
      } else {
        result.put("reason", "unknown");

        LOG.error("Could not create branch with name: " + branchName + ", for repo " + projectId);
      }

    } catch (GitAPIException e) {
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
      LOG.error("Could not create branch with name: " + branchName, e);
    } catch (Exception e) {
      result.put("reason", e.getMessage());
      result.put("detailedReason", e);
      LOG.error("Could not create branch with name: " + branchName, e);
    }

    return result;
  }


  public List<CustomRefModel> getCustomRefModels(final boolean updated) {
    List<CustomRefModel> customRefModels = new ArrayList<>();
    Repository repository = null;

    List<String> repoNames = getRepositoryListFromDB();

    if (CollectionUtils.isEmpty(repoNames)) {
      LOG.error("No repositories found in Database!!");
      return null;
    }

    for (String repoName : repoNames) {
      LOG.debug("Repository names loaded:" + repoName);
    }

    for (String repoName : repoNames) {

      boolean isRepoValidForWorking = checkIsRepoCloneStatusValidForWorking(repoName);

      if (!isRepoValidForWorking) {

        tryToFixRepository(repoName);
        LOG.debug("Clone status of Repository " + repoName
            + " is currenlty 'not completed', this not a valid repo for working!!");
        continue;
      }

      if (updated) {
        repository = getRepository(repoName, true);
      } else {
        repository = getRepository(repoName, false);
      }
      List<RefModel> branchModels = JGitUtils.getRemoteBranches(repository, false, -1);

      if (CollectionUtils.isNotEmpty(branchModels)) {
        for (RefModel model : branchModels) {

          // TODO cache CustomRefModel as it will be called from many places at many times
          CustomRefModel extModel = new CustomRefModel();
          extModel.setRepository(repository);
          extModel.setRepositoryName(repoName);
          extModel.setRefModel(model);

          customRefModels.add(extModel);
        }
      }
    }
    return customRefModels;
  }

  /**
   * <p>
   * this will check is the repo clone status currently not in - inProgress state
   * </p>
   * <p>
   * if it is in - inProgress state - leave as it is and return
   * </p>
   * <p>
   * if not, delete the non completed repo and try to start the cloning process again
   * </p>
   * 
   * @param repoName
   */
  private void tryToFixRepository(String repoName) {
    RepoCloneStatusModel repoStatusModel = repoCloneStatusDao.fetchEntity(repoName);

    if (repoStatusModel != null
        && CloneStatus.IN_PROGRESS.equals(CloneStatus.forName(repoStatusModel.getCloneStatus()))) {
      return;
    }
    removeRepositoryFolder(repoName);
    getUpdatedRepository(repoName, true);
  }

  private boolean checkIsRepoCloneStatusValidForWorking(String repoName) {
    List<RepoCloneStatusModel> repoStatusModels = repoCloneStatusDao.fetchAll();

    boolean isValid = false;
    repoName = repoName.toLowerCase();
    
    if (CollectionUtils.isEmpty(repoStatusModels)) {
      // if there is no entry then probably this is the first time when application has ran or the
      // repository has been save from outside the application,
      // thus created a initial status model and saving
      saveRepoCloneStatus(repoName);
      LOG.error("No Repo status entry found in DB!! Added one for " + repoName);
      return false;
    }

    for (RepoCloneStatusModel repoStatusModel : repoStatusModels) {
      if (repoName.equals(repoStatusModel.getRepoName())) {
        if (repoStatusModel != null
            && CloneStatus.forName(repoStatusModel.getCloneStatus()).equals(CloneStatus.COMPLETED)) {
          isValid = true;
          break;
        }
      }
    }

    return isValid;
  }

  @Override
  public void saveRepoCloneStatus(String repoName) {
    String repoNameLower = repoName.toLowerCase();
    RepoCloneStatusModel newRepoStatusModel = new RepoCloneStatusModel(repoNameLower);
    repoCloneStatusDao.saveOrUpdateEntity(newRepoStatusModel);
  }

  @Autowired
  public void setRepositoryDao(RepositoryDao repositoryDao) {
    repositoryDao.setClazz(RepositoryModel.class);
    this.repositoryDao = repositoryDao;
  }

  @Override
  public List<String> getTree(String commitId) throws MissingObjectException,
      IncorrectObjectTypeException, IOException {

    List<String> fileTree = new ArrayList<>();

    Repository r = getRepository("GitWebIntegration", false);

    RevCommit commit = null;
    RevTree tree = null;
    try (RevWalk walk = new RevWalk(r)) {
      commit = walk.parseCommit(r.resolve(commitId));

      tree = walk.parseTree(commit.getTree().getId());
      System.out.println("Found Tree: " + tree);

      walk.dispose();
    }

    TreeWalk treeWalk = new TreeWalk(r);
    treeWalk.reset(tree.getId());
    treeWalk.setRecursive(true);

    while (treeWalk.next()) {
      String path = treeWalk.getPathString();
      System.out.println("path:" + path);
      fileTree.add(path);
    }
    treeWalk.close();
    return fileTree;

  }

  @Override
  public List<PathModel> getTree2(String repositoryName, String path, String commitId)
      throws MissingObjectException, IncorrectObjectTypeException, IOException {
    Repository r = getRepository(repositoryName, false);

    RevCommit commit = null;
    try (RevWalk walk = new RevWalk(r)) {
      commit = walk.parseCommit(r.resolve(commitId));
      walk.dispose();
    }

    List<PathModel> paths = JGitUtils.getFilesInPath2(r, path, commit);

    if (path != null && path.trim().length() > 0) {
      // add .. parent path entry
      String parentPath = null;
      if (path.lastIndexOf('/') > -1) {
        parentPath = path.substring(0, path.lastIndexOf('/'));
      }
      PathModel model =
          new PathModel("..", parentPath, null, 0, FileMode.TREE.getBits(), null, commitId);
      model.isParentPath = true;
      paths.add(0, model);
    }

    // if (isDebugOn()) {
    // for (PathModel p : paths) {
    // String s =
    // p.name + "--" + p.path + "--" + p.mode + "--" + p.size + "--" + p.commitId + "--"
    // + p.objectId + "--" + p.isFile();
    // LOG.debug(s);
    // }
    //
    // }
    return paths;
  }

  @Override
  public Map<String, Object> getBlob(String repositoryName, String path, String commitId)
      throws RevisionSyntaxException, MissingObjectException, IncorrectObjectTypeException,
      AmbiguousObjectException, IOException {
    Repository r = getRepository(repositoryName, false);

    RevCommit commit = null;
    try (RevWalk walk = new RevWalk(r)) {
      commit = walk.parseCommit(r.resolve(commitId));
      walk.dispose();
    }

    Map<String, Object> result = blobStrategy.convert(path, r, commit, getSettings());

    return result;
  }

  @Override
  public Map<String, Object> getBlob(CommitTreeRequestForm form) throws RevisionSyntaxException,
      MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException, IOException {
    return getBlob(form.getProjectId(), form.getPath(), form.getCommitId());
  }

  @Override
  public synchronized void removeRepositoryFolder(final String repo) {
    File f = getRepositoriesFolder();
    File repoDir = new File(f, repo);
    try {
      File[] files = repoDir.listFiles();
      if (files != null) {
        for (File fe : files) {
          if (fe.isFile()) {
            fe.delete();
          } else {
            FileUtils.deleteDirectory(fe);
          }
        }
      }
      FileUtils.deleteDirectory(repoDir);
    } catch (IOException e) {
      LOG.error("Error occured reverting the repsositry folders " + repoDir, e);
    }
  }

  @Autowired
  public void setRepoCloneStatusDao(BaseDao<RepoCloneStatusModel> baseDao) {
    baseDao.setClazz(RepoCloneStatusModel.class);
    this.repoCloneStatusDao = baseDao;
  }
}
