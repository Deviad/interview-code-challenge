package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

@Value
@With
public class ProductId implements ValueObject<Long> {
  Long value;
}
