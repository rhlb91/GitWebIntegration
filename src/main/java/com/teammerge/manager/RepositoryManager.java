package com.teammerge.manager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.teammerge.Constants;
import com.teammerge.Constants.AccessRestrictionType;
import com.teammerge.Constants.AuthorizationControl;
import com.teammerge.Constants.CommitMessageRenderer;
import com.teammerge.Constants.MergeType;
import com.teammerge.IStoredSettings;
import com.teammerge.Keys;
import com.teammerge.model.ForkModel;
import com.teammerge.model.Metric;
import com.teammerge.model.RegistrantAccessPermission;
import com.teammerge.model.RepositoryModel;
import com.teammerge.model.UserModel;
import com.teammerge.utils.ArrayUtils;
import com.teammerge.utils.ByteFormat;
import com.teammerge.utils.DeepCopier;
import com.teammerge.utils.JGitUtils;
import com.teammerge.utils.JGitUtils.LastChange;
import com.teammerge.utils.StringUtils;

public class RepositoryManager implements IRepositoryManager {

  private final Logger          logger = LoggerFactory.getLogger(getClass());

  private final IStoredSettings settings;
  private File                  repositoriesFolder;
  private final IRuntimeManager runtimeManager;
  private final IUserManager    userManager;

  @Inject
  public RepositoryManager(IRuntimeManager runtimeManager, IUserManager userManager) {

    this.settings = runtimeManager.getSettings();
    this.runtimeManager = runtimeManager;
    this.userManager = userManager;

    repositoriesFolder =
        runtimeManager.getFileOrFolder(Keys.git.repositoriesFolder, "${baseFolder}/git");
  }

  public List<String> getRepositoryList() {
    List<String> repositories = null;
    if (!isValidRepositoryList()) {

      repositoriesFolder =
          runtimeManager.getFileOrFolder(Keys.git.repositoriesFolder, "${baseFolder}/git");
      logger.info("Repositories folder : {}", repositoriesFolder.getAbsolutePath());

      // we are not caching OR we have not yet cached OR the cached list
      // is invalid
      long startTime = System.currentTimeMillis();
      repositories =
          JGitUtils.getRepositoryList(repositoriesFolder,
              settings.getBoolean(Keys.git.onlyAccessBareRepositories, false),
              settings.getBoolean(Keys.git.searchRepositoriesSubfolders, true),
              settings.getInteger(Keys.git.searchRecursionDepth, -1),
              settings.getStrings(Keys.git.searchExclusions));

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
        /*
         * for (RepositoryModel model : repositoryListCache.values()) { if
         * (!StringUtils.isEmpty(model.originRepository)) { String originKey =
         * getRepositoryKey(model.originRepository); if (repositoryListCache.containsKey(originKey))
         * { RepositoryModel origin = repositoryListCache .get(originKey);
         * origin.addFork(model.name); } } }
         */

        long duration = System.currentTimeMillis() - startTime;
        logger.info(MessageFormat.format(msg, repositories.size(), duration));
      }
    }

    // return sorted copy of cached list
    /*
     * List<String> list = new ArrayList<String>(); for (RepositoryModel model :
     * repositoryListCache.values()) { list.add(model.name); }
     * StringUtils.sortRepositorynames(list);
     */
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
  public boolean hasRepository(String repositoryName, boolean caseSensitiveCheck) {
    /*
     * if (!caseSensitiveCheck && settings.getBoolean(Keys.git.cacheRepositoryList, true)) { // if
     * we are caching use the cache to determine availability // otherwise we end up adding a
     * phantom repository to the cache String key = getRepositoryKey(repositoryName); return
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
   * Compare the last repository list setting checksum to the current checksum. If different then
   * clear the cache so that it may be rebuilt.
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
    return runtimeManager.getFileOrFolder(Keys.git.repositoriesFolder, "${baseFolder}/git");
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
  public List<RegistrantAccessPermission> getUserAccessPermissions(UserModel user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RegistrantAccessPermission> getUserAccessPermissions(RepositoryModel repository) {
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
  public List<RegistrantAccessPermission> getTeamAccessPermissions(RepositoryModel repository) {
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
    return getRepository(repositoryName, true);
  }

  @Override
  public Repository getRepository(String name, boolean logError) {
    String repositoryName = fixRepositoryName(name);

    if (isCollectingGarbage(repositoryName)) {
      logger.warn(MessageFormat.format("Rejecting request for {0}, busy collecting garbage!",
          repositoryName));
      return null;
    }

    File dir = FileKey.resolve(new File(repositoriesFolder, repositoryName), FS.DETECTED);
    if (dir == null)
      return null;

    Repository r = null;
    try {
      FileKey key = FileKey.exact(dir, FS.DETECTED);
      r = RepositoryCache.open(key, true);
    } catch (IOException e) {
      if (logError) {
        logger.error("GitBlit.getRepository(String) failed to find "
            + new File(repositoriesFolder, repositoryName).getAbsolutePath());
      }
    }
    return r;
  }

  @Override
  public List<RepositoryModel> getRepositoryModels() {
    long methodStart = System.currentTimeMillis();
    List<String> list = getRepositoryList();
    List<RepositoryModel> repositories = new ArrayList<RepositoryModel>();
    for (String repo : list) {
      RepositoryModel model = getRepositoryModel(repo);
      if (model != null) {
        repositories.add(model);
      }
    }
    long duration = System.currentTimeMillis() - methodStart;
    logger.info(MessageFormat.format("{0} repository models loaded in {1} msecs", duration));
    return repositories;
  }

  @Override
  public List<RepositoryModel> getRepositoryModels(UserModel user) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RepositoryModel getRepositoryModel(UserModel user, String repositoryName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RepositoryModel getRepositoryModel(String name) {
    String repositoryName = fixRepositoryName(name);

    RepositoryModel model = loadRepositoryModel(repositoryName);
    if (model == null) {
      return null;
    }
    return DeepCopier.copy(model);
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
    LastChange lc = JGitUtils.getLastChange(r);
    model.setLastChange(lc.when);
    model.setLastChangeAuthor(lc.who);

    if (!settings.getBoolean(Keys.web.showRepositorySizes, true) || model.isSkipSizeCalculation()) {
      model.setSize(null);
      return 0L;
    }
    /* if (!repositorySizeCache.hasCurrent(model.name, model.lastChange)) { */
    File gitDir = r.getDirectory();
    long sz = com.teammerge.utils.FileUtils.folderSize(gitDir);
    /* repositorySizeCache.updateObject(model.name, model.lastChange, sz); */
    /* } */
    /* long size = repositorySizeCache.getObject(model.name); */
    long size = sz;
    ByteFormat byteFormat = new ByteFormat();
    model.setSize(byteFormat.format(size));
    return size;
  }

  @Override
  public List<Metric> getRepositoryDefaultMetrics(RepositoryModel model, Repository repository) {
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
  public List<String> getPostReceiveScriptsInherited(RepositoryModel repository) {
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

  /**
   * Create a repository model from the configuration and repository data.
   *
   * @param repositoryName
   * @return a repositoryModel or null if the repository does not exist
   */
  private RepositoryModel loadRepositoryModel(String repositoryName) {
    Repository r = getRepository(repositoryName);
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
        logger.error("Failed to determine fork for " + model, e);
      }
    }
    return model;
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
}
