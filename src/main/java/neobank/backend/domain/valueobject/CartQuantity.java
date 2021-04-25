package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

/*
   This is a value object
*/
@With
@Value
public class CartQuantity implements ValueObject<Long> {
  Long value;
}
