package neobank.backend.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CartStatus {
  ACTIVE("ACTIVE"),
  WAITING_FOR_PAYMENT("WAITING_FOR_PAYMENT"),
  PAID("PAID"),
  CHECKED_OUT("CHECKED_OUT");

  String value;
}
