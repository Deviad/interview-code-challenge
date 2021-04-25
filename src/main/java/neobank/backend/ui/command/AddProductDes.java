package neobank.backend.ui.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.SKU;

import java.util.Map;

@AllArgsConstructor
@Data // Used to Map to value object as if it was deserialized.
public class AddProductDes {
  Map<SKU, CartQuantity> products;
  String username;
}

// Jackson seems not to like Value Object at all.
// We are in luck with Mapstruct.
