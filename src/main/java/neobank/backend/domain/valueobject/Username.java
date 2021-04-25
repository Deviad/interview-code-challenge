package neobank.backend.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Value;
import lombok.With;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   This is a value object
*/

@Value
@With
public class Username implements ValueObject<String> {

  String value;

  @JsonCreator
  public Username(String value) {
    validate(value);
    this.value = value;
  }

  public void validate(String value) {

    if (value == null || value.equals(" ")) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    Pattern p = Pattern.compile("^[A-Za-z]{3,20}$");
    Matcher m = p.matcher(value);
    if (!m.matches()) {
      throw new IllegalArgumentException("Username cannot be accepted");
    }
  }
}
