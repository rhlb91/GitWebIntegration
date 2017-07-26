package com.teammerge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyClass;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.teammerge.Constants.AccessRestrictionType;
import com.teammerge.Constants.AuthorizationControl;
import com.teammerge.Constants.CommitMessageRenderer;
import com.teammerge.Constants.MergeType;
import com.teammerge.utils.ArrayUtils;
import com.teammerge.utils.ModelUtils;
import com.teammerge.utils.StringUtils;

/**
 * RepositoryModel is a serializable model class that represents a repository including its
 * configuration settings and access restriction.
 *
 *
 */

@Entity
@Table(name = "repository")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "entity")
public class RepositoryModel implements Serializable, Comparable<RepositoryModel> {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "name")
  private String name;

  @Column(name = "companyId")
  private String companyId;

  @Column(name = "description")
  private String description;

  @Column(name = "owners")
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> owners;

  @Column(name = "lastChange")
  private Date lastChange;

  @Column(name = "lastChangeAuthor")
  private String lastChangeAuthor;

  @Column(name = "hasCommits")
  private boolean hasCommits;

  private boolean showRemoteBranches;

  private boolean useIncrementalPushTags;

  private String incrementalPushTagPrefix;
  private AccessRestrictionType accessRestriction;
  private AuthorizationControl authorizationControl;
  private boolean allowAuthenticated;

  private boolean isFrozen;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> federationSets;

  private boolean isFederated;

  private boolean skipSizeCalculation;

  private boolean skipSummaryMetrics;

  private String frequency;

  private boolean isBare;

  private boolean isMirror;

  @Column(name = "origin")
  private String origin;

  @Column(name = "HEAD")
  private String HEAD;

  @Column(name = "size")
  private String size;

  @Column(name = "availableRefs", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> availableRefs;

  @Column(name = "indexedBranches", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> indexedBranches;

  @Column(name = "preReceiveScripts", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> preReceiveScripts;

  @Column(name = "postReceiveScripts", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> postReceiveScripts;

  @Column(name = "mailingLists", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> mailingLists;

  @Column(name = "customFields", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  @MapKeyClass(String.class)
  private Map<String, String> customFields;

  @Column(name = "projectPath", nullable = true)
  private String projectPath;

  @Column(name = "displayName")
  private String displayName;

  private boolean allowForks;

  @Column(name = "forks", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<String> forks;

  @Column(name = "originRepository")
  private String originRepository;

  @Column(name = "verifyCommitter", nullable = true)
  private boolean verifyCommitter;

  @Column(name = "gcThreshold", nullable = true)
  private String gcThreshold;

  @Column(name = "gcPeriod", nullable = true)
  private int gcPeriod;

  @Column(name = "maxActivityCommits", nullable = true)
  private int maxActivityCommits;

  @Column(name = "metricAuthorExclusions", nullable = true)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> metricAuthorExclusions;

  @Column(name = "commitMessageRenderer", nullable = true)
  private CommitMessageRenderer commitMessageRenderer;

  @Column(name = "acceptNewPatchsets", nullable = true)
  private boolean acceptNewPatchsets;

  @Column(name = "acceptNewTickets", nullable = true)
  private boolean acceptNewTickets;

  @Column(name = "requireApproval", nullable = true)
  private boolean requireApproval;

  @Column(name = "mergeTo", nullable = true)
  private String mergeTo;

  @Column(name = "mergeType", nullable = true)
  private MergeType mergeType;

  @Column(name = "isCollectingGarbage", nullable = true)
  private transient boolean isCollectingGarbage;

  @Column(name = "lastGC", nullable = true)
  private Date lastGC;

  @Column(name = "sparkleshareId", nullable = true)
  private String sparkleshareId;

  public RepositoryModel() {
    this("", "", "", new Date(0));
  }

  public RepositoryModel(String name, String description, String owner, Date lastchange) {
    this.name = name;
    this.description = description;
    this.lastChange = lastchange;
    this.accessRestriction = AccessRestrictionType.NONE;
    this.authorizationControl = AuthorizationControl.NAMED;
    this.federationSets = new ArrayList<String>();
    this.projectPath = StringUtils.getFirstPathElement(name);
    this.owners = new ArrayList<String>();
    this.isBare = true;
    this.acceptNewTickets = true;
    this.acceptNewPatchsets = true;
    this.mergeType = MergeType.DEFAULT_MERGE_TYPE;

    addOwner(owner);
  }

  public List<String> getLocalBranches() {
    if (ArrayUtils.isEmpty(availableRefs)) {
      return new ArrayList<String>();
    }
    List<String> localBranches = new ArrayList<String>();
    for (String ref : availableRefs) {
      if (ref.startsWith("refs/heads")) {
        localBranches.add(ref);
      }
    }
    return localBranches;
  }

  public void addFork(String repository) {
    if (forks == null) {
      forks = new TreeSet<String>();
    }
    forks.add(repository);
  }

  public void removeFork(String repository) {
    if (forks == null) {
      return;
    }
    forks.remove(repository);
  }

  public void resetDisplayName() {
    displayName = null;
  }

  public String getRID() {
    return StringUtils.getSHA1(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof RepositoryModel) {
      return name.equals(((RepositoryModel) o).name);
    }
    return false;
  }

  @Override
  public String toString() {
    if (displayName == null) {
      displayName = StringUtils.stripDotGit(name);
    }
    return displayName;
  }

  public int compareTo(RepositoryModel o) {
    return StringUtils.compareRepositoryNames(name, o.name);
  }

  public boolean isFork() {
    return !StringUtils.isEmpty(originRepository);
  }

  public boolean isOwner(String username) {
    if (StringUtils.isEmpty(username) || ArrayUtils.isEmpty(owners)) {
      return isUsersPersonalRepository(username);
    }
    return owners.contains(username.toLowerCase()) || isUsersPersonalRepository(username);
  }

  public boolean isPersonalRepository() {
    return !StringUtils.isEmpty(projectPath) && ModelUtils.isPersonalRepository(projectPath);
  }

  public boolean isUsersPersonalRepository(String username) {
    return !StringUtils.isEmpty(projectPath)
        && ModelUtils.isUsersPersonalRepository(username, projectPath);
  }

  public boolean allowAnonymousView() {
    return !accessRestriction.atLeast(AccessRestrictionType.VIEW);
  }

  public boolean isShowActivity() {
    return maxActivityCommits > -1;
  }

  public boolean isSparkleshared() {
    return !StringUtils.isEmpty(sparkleshareId);
  }

  public RepositoryModel cloneAs(String cloneName) {
    RepositoryModel clone = new RepositoryModel();
    clone.originRepository = name;
    clone.name = cloneName;
    clone.projectPath = StringUtils.getFirstPathElement(cloneName);
    clone.isBare = true;
    clone.description = description;
    clone.accessRestriction = AccessRestrictionType.PUSH;
    clone.authorizationControl = AuthorizationControl.NAMED;
    // clone.federationStrategy = federationStrategy;
    clone.showRemoteBranches = false;
    clone.allowForks = false;
    clone.acceptNewPatchsets = false;
    clone.acceptNewTickets = false;
    clone.skipSizeCalculation = skipSizeCalculation;
    clone.skipSummaryMetrics = skipSummaryMetrics;
    clone.sparkleshareId = sparkleshareId;
    return clone;
  }

  public void addOwner(String username) {
    if (!StringUtils.isEmpty(username)) {
      String name = username.toLowerCase();
      // a set would be more efficient, but this complicates JSON
      // deserialization so we enforce uniqueness with an arraylist
      if (!owners.contains(name)) {
        owners.add(name);
      }
    }
  }

  public void removeOwner(String username) {
    if (!StringUtils.isEmpty(username)) {
      owners.remove(username.toLowerCase());
    }
  }

  public void addOwners(Collection<String> usernames) {
    if (!ArrayUtils.isEmpty(usernames)) {
      for (String username : usernames) {
        addOwner(username);
      }
    }
  }

  public void removeOwners(Collection<String> usernames) {
    if (!ArrayUtils.isEmpty(owners)) {
      for (String username : usernames) {
        removeOwner(username);
      }
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getOwners() {
    return owners;
  }

  public void setOwners(List<String> owners) {
    this.owners = owners;
  }

  public Date getLastChange() {
    return lastChange;
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

  public String getLastChangeAuthor() {
    return lastChangeAuthor;
  }

  public void setLastChangeAuthor(String lastChangeAuthor) {
    this.lastChangeAuthor = lastChangeAuthor;
  }

  public boolean isHasCommits() {
    return hasCommits;
  }

  public void setHasCommits(boolean hasCommits) {
    this.hasCommits = hasCommits;
  }

  public boolean isShowRemoteBranches() {
    return showRemoteBranches;
  }

  public void setShowRemoteBranches(boolean showRemoteBranches) {
    this.showRemoteBranches = showRemoteBranches;
  }

  public boolean isUseIncrementalPushTags() {
    return useIncrementalPushTags;
  }

  public void setUseIncrementalPushTags(boolean useIncrementalPushTags) {
    this.useIncrementalPushTags = useIncrementalPushTags;
  }

  public String getIncrementalPushTagPrefix() {
    return incrementalPushTagPrefix;
  }

  public void setIncrementalPushTagPrefix(String incrementalPushTagPrefix) {
    this.incrementalPushTagPrefix = incrementalPushTagPrefix;
  }

  public AccessRestrictionType getAccessRestriction() {
    return accessRestriction;
  }

  public void setAccessRestriction(AccessRestrictionType accessRestriction) {
    this.accessRestriction = accessRestriction;
  }

  public AuthorizationControl getAuthorizationControl() {
    return authorizationControl;
  }

  public void setAuthorizationControl(AuthorizationControl authorizationControl) {
    this.authorizationControl = authorizationControl;
  }

  public boolean isAllowAuthenticated() {
    return allowAuthenticated;
  }

  public void setAllowAuthenticated(boolean allowAuthenticated) {
    this.allowAuthenticated = allowAuthenticated;
  }

  public boolean isFrozen() {
    return isFrozen;
  }

  public void setFrozen(boolean isFrozen) {
    this.isFrozen = isFrozen;
  }

  public List<String> getFederationSets() {
    return federationSets;
  }

  public void setFederationSets(List<String> federationSets) {
    this.federationSets = federationSets;
  }

  public boolean isFederated() {
    return isFederated;
  }

  public void setFederated(boolean isFederated) {
    this.isFederated = isFederated;
  }

  public boolean isSkipSizeCalculation() {
    return skipSizeCalculation;
  }

  public void setSkipSizeCalculation(boolean skipSizeCalculation) {
    this.skipSizeCalculation = skipSizeCalculation;
  }

  public boolean isSkipSummaryMetrics() {
    return skipSummaryMetrics;
  }

  public void setSkipSummaryMetrics(boolean skipSummaryMetrics) {
    this.skipSummaryMetrics = skipSummaryMetrics;
  }

  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public boolean isBare() {
    return isBare;
  }

  public void setBare(boolean isBare) {
    this.isBare = isBare;
  }

  public boolean isMirror() {
    return isMirror;
  }

  public void setMirror(boolean isMirror) {
    this.isMirror = isMirror;
  }

  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getHEAD() {
    return HEAD;
  }

  public void setHEAD(String hEAD) {
    HEAD = hEAD;
  }

  public List<String> getAvailableRefs() {
    return availableRefs;
  }

  public void setAvailableRefs(List<String> availableRefs) {
    this.availableRefs = availableRefs;
  }

  public List<String> getIndexedBranches() {
    return indexedBranches;
  }

  public void setIndexedBranches(List<String> indexedBranches) {
    this.indexedBranches = indexedBranches;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public List<String> getPreReceiveScripts() {
    return preReceiveScripts;
  }

  public void setPreReceiveScripts(List<String> preReceiveScripts) {
    this.preReceiveScripts = preReceiveScripts;
  }

  public List<String> getPostReceiveScripts() {
    return postReceiveScripts;
  }

  public void setPostReceiveScripts(List<String> postReceiveScripts) {
    this.postReceiveScripts = postReceiveScripts;
  }

  public List<String> getMailingLists() {
    return mailingLists;
  }

  public void setMailingLists(List<String> mailingLists) {
    this.mailingLists = mailingLists;
  }

  public Map<String, String> getCustomFields() {
    return customFields;
  }

  public void setCustomFields(Map<String, String> customFields) {
    this.customFields = customFields;
  }

  public String getProjectPath() {
    return projectPath;
  }

  public void setProjectPath(String projectPath) {
    this.projectPath = projectPath;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isAllowForks() {
    return allowForks;
  }

  public void setAllowForks(boolean allowForks) {
    this.allowForks = allowForks;
  }

  public Set<String> getForks() {
    return forks;
  }

  public void setForks(Set<String> forks) {
    this.forks = forks;
  }

  public String getOriginRepository() {
    return originRepository;
  }

  public void setOriginRepository(String originRepository) {
    this.originRepository = originRepository;
  }

  public boolean isVerifyCommitter() {
    return verifyCommitter;
  }

  public void setVerifyCommitter(boolean verifyCommitter) {
    this.verifyCommitter = verifyCommitter;
  }

  public String getGcThreshold() {
    return gcThreshold;
  }

  public void setGcThreshold(String gcThreshold) {
    this.gcThreshold = gcThreshold;
  }

  public int getGcPeriod() {
    return gcPeriod;
  }

  public void setGcPeriod(int gcPeriod) {
    this.gcPeriod = gcPeriod;
  }

  public int getMaxActivityCommits() {
    return maxActivityCommits;
  }

  public void setMaxActivityCommits(int maxActivityCommits) {
    this.maxActivityCommits = maxActivityCommits;
  }

  public List<String> getMetricAuthorExclusions() {
    return metricAuthorExclusions;
  }

  public void setMetricAuthorExclusions(List<String> metricAuthorExclusions) {
    this.metricAuthorExclusions = metricAuthorExclusions;
  }

  public CommitMessageRenderer getCommitMessageRenderer() {
    return commitMessageRenderer;
  }

  public void setCommitMessageRenderer(CommitMessageRenderer commitMessageRenderer) {
    this.commitMessageRenderer = commitMessageRenderer;
  }

  public boolean isAcceptNewPatchsets() {
    return acceptNewPatchsets;
  }

  public void setAcceptNewPatchsets(boolean acceptNewPatchsets) {
    this.acceptNewPatchsets = acceptNewPatchsets;
  }

  public boolean isAcceptNewTickets() {
    return acceptNewTickets;
  }

  public void setAcceptNewTickets(boolean acceptNewTickets) {
    this.acceptNewTickets = acceptNewTickets;
  }

  public boolean isRequireApproval() {
    return requireApproval;
  }

  public void setRequireApproval(boolean requireApproval) {
    this.requireApproval = requireApproval;
  }

  public String getMergeTo() {
    return mergeTo;
  }

  public void setMergeTo(String mergeTo) {
    this.mergeTo = mergeTo;
  }

  public MergeType getMergeType() {
    return mergeType;
  }

  public void setMergeType(MergeType mergeType) {
    this.mergeType = mergeType;
  }

  public boolean isCollectingGarbage() {
    return isCollectingGarbage;
  }

  public void setCollectingGarbage(boolean isCollectingGarbage) {
    this.isCollectingGarbage = isCollectingGarbage;
  }

  public Date getLastGC() {
    return lastGC;
  }

  public void setLastGC(Date lastGC) {
    this.lastGC = lastGC;
  }

  public String getSparkleshareId() {
    return sparkleshareId;
  }

  public void setSparkleshareId(String sparkleshareId) {
    this.sparkleshareId = sparkleshareId;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
