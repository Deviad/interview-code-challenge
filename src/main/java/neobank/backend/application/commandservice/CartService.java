package neobank.backend.application.commandservice;

import io.vavr.API;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neobank.backend.adapter.CartAdapter;
import neobank.backend.adapter.CartProductAdapter;
import neobank.backend.adapter.ProductAdapter;
import neobank.backend.domain.aggregate.CartAggregate;
import neobank.backend.domain.aggregate.ProductAggregate;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.CartStatus;
import neobank.backend.domain.valueobject.SKU;
import neobank.backend.persistence.entity.Cart;
import neobank.backend.persistence.entity.CartProduct;
import neobank.backend.persistence.entity.Product;
import neobank.backend.persistence.repository.CartProductRepository;
import neobank.backend.persistence.repository.CartRepository;
import neobank.backend.persistence.repository.ProductRepository;
import neobank.backend.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final CartProductRepository cartProductRepository;
  private final ProductRepository productRepository;
  private final CartAdapter cartAdapter;
  private final CartProductAdapter cartProductAdapter;
  private final ProductAdapter productAdapter;

  public Cart addCartIfMissing(String username) {
    log.info("==== TRANSACTION 2: START");
    new RuntimeException("TRANS 2 ERROR").printStackTrace();
    var user = userRepository.findUserByUsername(username);
    var cart = cartRepository.findCartByUserIdAndCartStatusActive(user);

    if (cart == null) {
      var newCart = new Cart();
      newCart.setCartStatus(CartStatus.ACTIVE);
      user.getCarts().add(newCart);
      return userRepository.save(user).getCarts().stream()
          .filter(x -> x.getCartStatus().equals(CartStatus.ACTIVE))
          .findFirst()
          .get();
    }
    log.info("==== TRANSACTION 2: END");
    return cart;
  }

  @Transactional
  public void updateCart(String username, Map<SKU, CartQuantity> products) {
    new RuntimeException("1 ERROR").printStackTrace();
    //        log.info("==== TRANSACTION 1: START");

    Cart activeCart = Optional.of(this.addCartIfMissing(username)).orElseThrow();

    final CartAggregate cartWithUpdatedPrice =
        updateFinalPrice(
            products, updateProductQuantities(products, obtainUserActiveCart(username)));

    final Cart cart = cartAdapter.aggregateToEntity(cartWithUpdatedPrice, activeCart);
    final Cart savedCart = cartRepository.save(cart);

    this.changeProductQuantityAndSaveCart(username, products, savedCart);

    //        log.info("==== TRANSACTION 1: END");
  }

  /*
     Dealing with hibernate composite keys is problematic, therefore I have used a surrogate key.
     By using a surrogate key I had to create a merging logic so that if a product is already
     present in the table cart_product then I just update the price, otherwise I add a new row.
     If product price = 0 or an entry with that id is missing the product is removed from the table.
  */

  void changeProductQuantityAndSaveCart(
      String username, Map<SKU, CartQuantity> productsChangeRequest, Cart activeCart) {
    log.info("==== TRANSACTION 3: START");
    //        new RuntimeException("2 ERROR").printStackTrace();
    CartAggregate cartAggregate = obtainUserActiveCart(username);
    final CartAggregate cWUpdatedPriceAndProducts =
        updateProductQuantities(productsChangeRequest, cartAggregate);
    var productsInCart = cartProductRepository.findAllByCartId(activeCart.getId());
    var productsFromRequest =
        createCartProductEntities(cWUpdatedPriceAndProducts, activeCart, productsInCart);

    Map<Long, CartQuantity> productIdAndQuantity =
        mapProductAggregateWithCartQuantity(productsChangeRequest).entrySet().stream()
            .map(x -> Map.entry(x.getKey().productId().getValue(), x.getValue()))
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

    var productsToRemove =
        productsInCart.stream()
            .filter(
                x ->
                    productIdAndQuantity.get(x.getProduct().getId()) == null
                        || x.getQuantity() == 0)
            .collect(Collectors.toList());

    cartProductRepository.saveAll(productsFromRequest);
    cartProductRepository.deleteAll(productsToRemove);
    log.info("==== TRANSACTION 3: END");
  }

  CartAggregate updateFinalPrice(
      Map<SKU, CartQuantity> productsChangeRequest, CartAggregate cartAggregate) {

    var result = API.CheckedFunction(cartAggregate::clone).unchecked().get();

    Map<ProductAggregate, CartQuantity> map =
        mapProductAggregateWithCartQuantity(productsChangeRequest);

    map.values().forEach(result::updateFinalPrice);
    return result;
  }

  CartAggregate updateProductQuantities(
      Map<SKU, CartQuantity> productsChangeRequest, CartAggregate cartAggregate) {
    var result = API.CheckedFunction(cartAggregate::clone).unchecked().get();
    Map<ProductAggregate, CartQuantity> map =
        mapProductAggregateWithCartQuantity(productsChangeRequest);
    map.forEach(result::changeProductQuantity);
    return result;
  }

  private HashMap<ProductAggregate, CartQuantity> mapProductAggregateWithCartQuantity(
      Map<SKU, CartQuantity> productsChangeRequest) {
    return fetchProductEntities(productsChangeRequest).stream()
        .map(
            x -> {
              var cq = productsChangeRequest.get(new SKU(x.getSku()));
              return Map.entry(productAdapter.entityToAggregate(x), cq);
            })
        .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
  }

  private List<CartProduct> createCartProductEntities(
      CartAggregate cartAggregate, Cart activeCart, List<CartProduct> productsInCart) {

    HashMap<Long, CartProduct> mappedProducts =
        productsInCart.stream()
            .map(x -> Map.entry(x.getProduct().getId(), x))
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

    Map<Long, Tuple2<Long, Product>> productsFromRequest =
        cartAggregate.showCartProducts().entrySet().stream()
            .map(
                x ->
                    Map.entry(
                        x.getKey().productId().getValue(),
                        new Tuple2<>(
                            x.getValue().getValue(), productAdapter.aggregateToEntity(x.getKey()))))
            .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

    return productsFromRequest.entrySet().stream()
        .map(
            x -> {
              if (mappedProducts.get(x.getKey()) != null) {
                mappedProducts.get(x.getKey()).setQuantity(x.getValue()._1());
                return mappedProducts.get(x.getKey());
              }
              return CartProduct.builder()
                  .cart(activeCart)
                  .product(x.getValue()._2)
                  .quantity(x.getValue()._1())
                  .build();
            })
        .collect(Collectors.toList());
  }

  private List<Product> fetchProductEntities(Map<SKU, CartQuantity> products) {
    var pList =
        productRepository.findAllBySkuIn(
            products.keySet().stream().map(SKU::getValue).collect(Collectors.toSet()));
    if (pList.size() == 0) {
      throw new IllegalArgumentException("Invalid products");
    }
    return pList;
  }

  @Transactional
  public void checkout(String username) {
    CartAggregate cartAggregate = obtainUserActiveCart(username);
    cartAggregate.checkout();

    var cart =
        cartRepository.findCartByUserIdAndCartStatusActive(
            userRepository.findUserByUsername(username));
    final Cart entity = cartAdapter.aggregateToEntity(cartAggregate, cart);
    cartRepository.save(entity);
  }

  private CartAggregate obtainUserActiveCart(String username) {
    var user = userRepository.findUserByUsername(username);

    var cart = cartRepository.findCartByUserIdAndCartStatusActive(user);

    if (cart == null) {
      throw new RuntimeException("First add a product and then checkout");
    }

    return cartAdapter.entityToAggregate(cart);
  }
}
