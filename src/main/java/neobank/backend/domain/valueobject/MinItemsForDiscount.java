package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

@With
@Value
public class MinItemsForDiscount implements ValueObject<Integer> {
  Integer value;
}
