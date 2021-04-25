package neobank.backend.infrastructure;

public class WrappedException extends IllegalArgumentException {

  public WrappedException(String message, Throwable cause) {
    super(message, cause);
  }
}
