package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

@Value
@With
public class ProductName implements ValueObject<String> {

  String value;
}
