package neobank.backend.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MappingUtils {

  public static final ObjectMapper MAPPER =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  /** Method to convert object data to json. */
  @SneakyThrows(JsonProcessingException.class)
  public static String toJson(Object data) {
    return data == null ? null : MAPPER.writeValueAsString(data);
  }
}
