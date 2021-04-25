package neobank.backend.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   This is a value object
*/
@With
@Getter
@ToString
@EqualsAndHashCode
public class Password implements ValueObject<String> {
  String value;

  @JsonCreator
  public Password(String value) {
    validate(value);
    this.value = value;
  }

  public void validate(String value) {
    Pattern p =
        Pattern.compile(
            "(?=.*[a-z]+)(?=.*[0-9]+)(?=.*[A-Z]+)(?=.*[!@#$%^&*()_+\\[\\]{}:\";,.<>?|=-_]+).{8,20}");
    Matcher m = p.matcher(value);
    if (!m.matches()) {
      throw new IllegalArgumentException("Password cannot be accepted");
    }
  }
}
