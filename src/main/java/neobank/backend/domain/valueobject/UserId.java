package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

@Value
@With
public class UserId implements ValueObject<Long> {
  Long value;
}
