package neobank.backend.domain.valueobject;

import lombok.ToString;
import lombok.Value;
import lombok.With;

import java.util.UUID;

/*
   This is a value object
*/
@With
@Value
public class SKU implements ValueObject<UUID> {
  @ToString.Exclude UUID value;
}
