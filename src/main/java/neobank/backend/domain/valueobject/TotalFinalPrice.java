package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

/*
   This is a value object
*/
@With
@Value
public class TotalFinalPrice implements ValueObject<Double> {

  Double value;
}
