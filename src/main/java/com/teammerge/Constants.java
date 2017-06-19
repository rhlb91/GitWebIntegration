package com.teammerge;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Constants {
  public static final String NAME                           = "Gitblit";

  public static final String DEFAULT_USER_REPOSITORY_PREFIX = "~";
  public static final String REGEX_SHA256                   = "[a-fA-F0-9]{64}";
  public static final String EXTERNAL_ACCOUNT               = "#externalAccount";

  public static final int    LEN_FILESTORE_META_MIN         = 125;

  public static final int    LEN_FILESTORE_META_MAX         = 146;

  public static final String R_PATH                         = "/r/";

  public static final String R_LFS                          = "info/lfs/";

  public static final String ENCODING                       = "UTF-8";
  public static final String R_TICKETS_PATCHSETS            = "refs/tickets/";
  public static final String R_TICKET                       = "refs/heads/ticket/";
  public static final String baseFolder                     = "baseFolder";
  public static final String baseFolder$                    = "${" + baseFolder + "}";

  public static final String CONFIG_GITBLIT                 = "gitblit";

  public static final String ISO8601                        = "yyyy-MM-dd'T'HH:mm:ssZ";

  public static final String CONFIG_CUSTOM_FIELDS           = "customFields";
  public static final String R_HEADS                        = "refs/heads/";
  public static final String R_TAGS                         = "refs/tags/";
  public static final String R_PULL                         = "refs/pull/";
  public static final int    LEN_SHORTLOG_REFS              = 60;
  public static final int    LEN_SHORTLOG                   = 78;

  public static String getVersion() {
    String v = Constants.class.getPackage().getImplementationVersion();
    if (v == null) {
      return "0.0.0-SNAPSHOT";
    }
    return v;
  }

  /**
   * This regular expression is used when searching for "mentions" in tickets (when someone writes
   * @thisOtherUser)
   */
  public static final String REGEX_TICKET_MENTION = "\\B@(?<user>[^\\s]+)\\b";

  public static String getBuildDate() {
    return getManifestValue("build-date", "PENDING");
  }

  private static String getManifestValue(String attrib, String defaultValue) {
    Class<?> clazz = Constants.class;
    String className = clazz.getSimpleName() + ".class";
    String classPath = clazz.getResource(className).toString();
    if (!classPath.startsWith("jar")) {
      // Class not from JAR
      return defaultValue;
    }
    try {
      String manifestPath =
          classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
      Manifest manifest = new Manifest(new URL(manifestPath).openStream());
      Attributes attr = manifest.getMainAttributes();
      String value = attr.getValue(attrib);
      return value;
    } catch (Exception e) {
    }
    return defaultValue;
  }

  public static enum CommitMessageRenderer {
    PLAIN, MARKDOWN;

    public static CommitMessageRenderer fromName(String name) {
      for (CommitMessageRenderer renderer : values()) {
        if (renderer.name().equalsIgnoreCase(name)) {
          return renderer;
        }
      }
      return CommitMessageRenderer.PLAIN;
    }
  }

  /**
   * The type of merge Gitblit will use when merging a ticket to the integration branch.
   * <p>
   * The default type is MERGE_ALWAYS.
   * <p>
   * This is modeled after the Gerrit SubmitType.
   */
  public static enum MergeType {
    /**
     * Allows a merge only if it can be fast-forward merged into the integration branch.
     */
    FAST_FORWARD_ONLY,
    /**
     * Uses a fast-forward merge if possible, other wise a merge commit is created.
     */
    MERGE_IF_NECESSARY,
    // Future REBASE_IF_NECESSARY,
    /**
     * Always merge with a merge commit, even when a fast-forward would be possible.
     */
    MERGE_ALWAYS,
    // Future? CHERRY_PICK
    ;

    public static final MergeType DEFAULT_MERGE_TYPE = MERGE_ALWAYS;

    public static MergeType fromName(String name) {
      for (MergeType type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return DEFAULT_MERGE_TYPE;
    }
  }

  public static enum AccountType {
    LOCAL, CONTAINER, LDAP, REDMINE, SALESFORCE, WINDOWS, PAM, HTPASSWD, HTTPHEADER;

    public static AccountType fromString(String value) {
      for (AccountType type : AccountType.values()) {
        if (type.name().equalsIgnoreCase(value)) {
          return type;
        }
      }
      return AccountType.LOCAL;
    }

    public boolean isLocal() {
      return this == LOCAL;
    }
  }

  /**
   * The access permissions available for a repository.
   */
  public static enum AccessPermission {
    NONE("N"), EXCLUDE("X"), VIEW("V"), CLONE("R"), PUSH("RW"), CREATE("RWC"), DELETE("RWD"), REWIND(
        "RW+"), OWNER("RW+");

    public static final AccessPermission[] NEWPERMISSIONS = {EXCLUDE, VIEW, CLONE, PUSH, CREATE,
                                                              DELETE, REWIND};

    public static final AccessPermission[] SSHPERMISSIONS = {VIEW, CLONE, PUSH};

    public static AccessPermission         LEGACY         = REWIND;

    public final String                    code;

    private AccessPermission(String code) {
      this.code = code;
    }

    public boolean atMost(AccessPermission perm) {
      return ordinal() <= perm.ordinal();
    }

    public boolean atLeast(AccessPermission perm) {
      return ordinal() >= perm.ordinal();
    }

    public boolean exceeds(AccessPermission perm) {
      return ordinal() > perm.ordinal();
    }

    public String asRole(String repository) {
      return code + ":" + repository;
    }

    @Override
    public String toString() {
      return code;
    }

    public static AccessPermission permissionFromRole(String role) {
      String[] fields = role.split(":", 2);
      if (fields.length == 1) {
        // legacy/undefined assume full permissions
        return AccessPermission.LEGACY;
      } else {
        // code:repository
        return AccessPermission.fromCode(fields[0]);
      }
    }

    public static String repositoryFromRole(String role) {
      String[] fields = role.split(":", 2);
      if (fields.length == 1) {
        // legacy/undefined assume full permissions
        return role;
      } else {
        // code:repository
        return fields[1];
      }
    }

    public static AccessPermission fromCode(String code) {
      for (AccessPermission perm : values()) {
        if (perm.code.equalsIgnoreCase(code)) {
          return perm;
        }
      }
      return AccessPermission.NONE;
    }
  }

  public static enum RegistrantType {
    REPOSITORY, USER, TEAM;
  }

  public static enum PermissionType {
    MISSING, ANONYMOUS, EXPLICIT, TEAM, REGEX, OWNER, ADMINISTRATOR;
  }

  /**
   * Enumeration representing the four access restriction levels.
   */
  public static enum AccessRestrictionType {
    NONE, PUSH, CLONE, VIEW;

    private static final AccessRestrictionType[] AUTH_TYPES = {PUSH, CLONE, VIEW};

    public static AccessRestrictionType fromName(String name) {
      for (AccessRestrictionType type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return NONE;
    }

    public static List<AccessRestrictionType> choices(boolean allowAnonymousPush) {
      if (allowAnonymousPush) {
        return Arrays.asList(values());
      }
      return Arrays.asList(AUTH_TYPES);
    }

    public boolean exceeds(AccessRestrictionType type) {
      return this.ordinal() > type.ordinal();
    }

    public boolean atLeast(AccessRestrictionType type) {
      return this.ordinal() >= type.ordinal();
    }

    @Override
    public String toString() {
      return name();
    }

    public boolean isValidPermission(AccessPermission permission) {
      switch (this) {
        case VIEW:
          // VIEW restriction
          // all access permissions are valid
          return true;
        case CLONE:
          // CLONE restriction
          // only CLONE or greater access permissions are valid
          return permission.atLeast(AccessPermission.CLONE);
        case PUSH:
          // PUSH restriction
          // only PUSH or greater access permissions are valid
          return permission.atLeast(AccessPermission.PUSH);
        case NONE:
          // NO access restriction
          // all access permissions are invalid
          return false;
      }
      return false;
    }
  }

  /**
   * Enumeration representing the types of authorization control for an access restricted resource.
   */
  public static enum AuthorizationControl {
    AUTHENTICATED, NAMED;

    public static AuthorizationControl fromName(String name) {
      for (AuthorizationControl type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return NAMED;
    }

    @Override
    public String toString() {
      return name();
    }
  }

  /**
   * Enumeration of the search types.
   */
  public static enum SearchType {
    AUTHOR, COMMITTER, COMMIT;

    public static SearchType forName(String name) {
      for (SearchType type : values()) {
        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
      return COMMIT;
    }

    @Override
    public String toString() {
      return name().toLowerCase();
    }
  }

  public static String getGitBlitVersion() {
    return NAME + " v" + getVersion();
  }
}
