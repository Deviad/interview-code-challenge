package neobank.backend.adapter;

import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.SKU;
import neobank.backend.ui.command.AddProduct;
import neobank.backend.ui.command.AddProductDes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface AddProductAdapter {

  @Mapping(target = "products", expression = "java(mapProducts(addProduct))")
  AddProductDes map(AddProduct addProduct);

  default Map<SKU, CartQuantity> mapProducts(AddProduct addProduct) {
    return Optional.ofNullable(addProduct.getProducts())
        .orElse(Collections.emptyMap())
        .entrySet()
        .stream()
        .map(x -> new AbstractMap.SimpleEntry<>(new SKU(UUID.fromString(x.getKey())), x.getValue()))
        .collect(
            HashMap::new, (m, e) -> m.put(e.getKey(), new CartQuantity(e.getValue())), Map::putAll);
  }
}
