package neobank.backend.domain.valueobject;

import lombok.Value;
import lombok.With;

/*
   This is a value object
*/

@Value
@With
/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
public class Quantity implements ValueObject<Long> {

  Long value;
}
