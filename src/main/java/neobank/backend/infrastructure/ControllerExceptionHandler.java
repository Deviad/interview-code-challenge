package neobank.backend.infrastructure;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Arrays.asList;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHandler {

  private static final List<String> ERROR_FIELDS = asList("error", "message");

  @ExceptionHandler
  public void onException(HttpServletResponse response, Throwable originalEx) {
    Throwable rootEx = firstNonNull(getRootCause(originalEx), originalEx);

    if (rootEx instanceof IllegalArgumentException) {
      String message = getMessage(originalEx, rootEx, rootEx.getMessage());
      log.error("Cannot process client request:", rootEx);
      sendError(response, SC_BAD_REQUEST, message);
    } else {
      String message = getMessage(originalEx, rootEx, rootEx.getMessage());
      log.error("Internal server error:", rootEx);
      sendError(response, SC_INTERNAL_SERVER_ERROR, message);
    }
  }

  private String getMessage(Throwable originalEx, Throwable rootEx, String rootMessage) {
    if (originalEx instanceof WrappedException) {
      return originalEx.getMessage();
    }

    return rootEx != originalEx && StringUtils.hasText(originalEx.getMessage())
        ? String.format("%s %s", originalEx.getMessage(), rootMessage)
        : rootMessage;
  }

  @SneakyThrows
  private void sendError(HttpServletResponse resp, int status, @Nullable String message) {
    resp.setStatus(status);
    resp.setContentType(APPLICATION_JSON_VALUE);

    try (PrintWriter writer = resp.getWriter()) {
      if (message != null) {
        writer.write(MappingUtils.toJson(Map.ofEntries(Map.entry("error", extractError(message)))));
      }
      writer.flush();
    }
  }

  private String extractError(@NonNull String message) {
    Map<?, ?> map;

    try {
      map = MappingUtils.MAPPER.readValue(message, Map.class);
    } catch (Exception ex) {
      return message;
    }

    return ERROR_FIELDS.stream()
        .map(map::get)
        .filter(value -> value instanceof String)
        .findFirst()
        .map(String.class::cast)
        .orElse(message);
  }
}
