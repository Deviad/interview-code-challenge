package neobank.backend.adapter;

import neobank.backend.domain.aggregate.ProductAggregate;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.persistence.entity.Cart;
import neobank.backend.persistence.entity.CartProduct;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface CartProductAdapter {

  default Set<CartProduct> mapCartProducts(
      Map<ProductAggregate, CartQuantity> cartProducts, Cart cart) {
    var mapper = Mappers.getMapper(ProductAdapter.class);
    return Optional.of(cartProducts.entrySet()).stream()
        .flatMap(Collection::stream)
        .map(
            x ->
                CartProduct.builder()
                    .cart(cart)
                    .product(mapper.aggregateToEntity(x.getKey()))
                    .quantity(x.getValue().getValue())
                    .build())
        .collect(Collectors.toSet());
  }
  ;
}
