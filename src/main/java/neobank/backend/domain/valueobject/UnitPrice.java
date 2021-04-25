package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

/*
   This is a value object
*/

@Value
@With
public class UnitPrice implements ValueObject<Double> {

  Double value;
}
