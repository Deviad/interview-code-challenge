package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

@With
@Value
public class CartId implements ValueObject<Long> {
  Long value;
}
