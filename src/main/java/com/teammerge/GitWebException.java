package com.teammerge;



/**
 * GitWebException is a marginally useful class.
 *
 *
 */
public class GitWebException extends Exception {

  private static final long serialVersionUID = 1L;

  public GitWebException(String message) {
    super(message);
  }

  public GitWebException(Throwable cause) {
    super(cause);
  }

  /**
   * Exception to indicate that the client should prompt for credentials because the requested
   * action requires authentication.
   */
  public static class UnauthorizedException extends GitWebException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
      super(message);
    }
  }

  /**
   * Exception to indicate that the requested action can not be executed by the specified user.
   */
  public static class ForbiddenException extends GitWebException {

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
      super(message);
    }
  }

  /**
   * Exception to indicate that the requested action has been disabled on the Gitblit server.
   */
  public static class NotAllowedException extends GitWebException {

    private static final long serialVersionUID = 1L;

    public NotAllowedException(String message) {
      super(message);
    }
  }

  /**
   * Exception to indicate that the requested action can not be executed by the server because it
   * does not recognize the request type.
   */
  public static class UnknownRequestException extends GitWebException {

    private static final long serialVersionUID = 1L;

    public UnknownRequestException(String message) {
      super(message);
    }
  }

  /**
   * Exception to indicate that the requested action can not be executed by the server because
   * argument provided is/are not valid.
   */
  public static class InvalidArgumentsException extends GitWebException {

    private static final long serialVersionUID = 1L;

    public InvalidArgumentsException(String message) {
      super(message);
    }
  }
}
