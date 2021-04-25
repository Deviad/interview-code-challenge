package neobank.backend.adapter;

import neobank.backend.domain.aggregate.CartAggregate;
import neobank.backend.domain.aggregate.ProductAggregate;
import neobank.backend.domain.aggregate.UserAggregate;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.persistence.entity.Cart;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mapper(
    componentModel = "spring",
    uses = {UserAggregate.class, ProductAdapter.class, ValueObjectMapper.class})
public interface CartAdapter {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "user", target = "userAggregate")
  @Mapping(source = "totalFinalPrice", target = "totalFinalPrice")
  @Mapping(target = "cartProducts", expression = "java(cartProductsToAggregate(cart))")
  CartAggregate entityToAggregate(Cart cart);

  @Mapping(source = "userAggregate", target = "user")
  @Mapping(target = "cartProducts", ignore = true)
  Cart aggregateToEntity(CartAggregate aggregate, @Context Cart cart);

  default HashMap<ProductAggregate, CartQuantity> cartProductsToAggregate(Cart cart) {

    var mapper = Mappers.getMapper(ProductAdapter.class);

    return Optional.ofNullable(cart.getCartProducts()).stream()
        .flatMap(Collection::stream)
        .map(
            x ->
                Map.entry(
                    mapper.entityToAggregate(x.getProduct()), new CartQuantity(x.getQuantity())))
        .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
  }
}
