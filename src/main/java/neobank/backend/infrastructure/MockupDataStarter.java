package neobank.backend.infrastructure;

import lombok.RequiredArgsConstructor;
import neobank.backend.application.commandservice.CartService;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.CartStatus;
import neobank.backend.domain.valueobject.ProductType;
import neobank.backend.domain.valueobject.SKU;
import neobank.backend.persistence.entity.Cart;
import neobank.backend.persistence.entity.Product;
import neobank.backend.persistence.entity.User;
import neobank.backend.persistence.repository.CartProductRepository;
import neobank.backend.persistence.repository.CartRepository;
import neobank.backend.persistence.repository.ProductRepository;
import neobank.backend.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MockupDataStarter {

  private final CartProductRepository cartProductRepository;
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartService cartService;

  @Autowired MockupDataStarter selfProxy;

  @EventListener()
  public void doSomethingAfterStartup(ApplicationReadyEvent event) {
    User user = selfProxy.createUser();

    final List<Product> savedProducts = selfProxy.saveProducts();

    var savedRolex =
        savedProducts.stream()
            .filter(x -> x.getProductName().equals("Rolex"))
            .findFirst()
            .orElse(null);

    Map<SKU, CartQuantity> map =
        Map.ofEntries(Map.entry(new SKU(savedRolex.getSku()), new CartQuantity(200L)));

    cartService.updateCart(user.getUsername(), map);
  }

  @Transactional
  List<Product> saveProducts() {
    var rolex =
        Product.builder()
            .unitPrice(100.00)
            .numberOfFreeItems(3)
            .minItemsForDiscount(200)
            .productType(ProductType.WATCH)
            .sku(UUID.randomUUID())
            .stockQuantity(400L)
            .productName("Rolex")
            .build();
    var kors =
        Product.builder()
            .unitPrice(80.00)
            .numberOfFreeItems(2)
            .minItemsForDiscount(100)
            .productType(ProductType.WATCH)
            .stockQuantity(400L)
            .sku(UUID.randomUUID())
            .productName("Michael Kors")
            .build();
    var swatch =
        Product.builder()
            .unitPrice(50.00)
            .productName("Swatch")
            .sku(UUID.randomUUID())
            .productType(ProductType.WATCH)
            .stockQuantity(400L)
            .build();
    var casio =
        Product.builder()
            .unitPrice(30.00)
            .productName("Casio")
            .productType(ProductType.WATCH)
            .sku(UUID.randomUUID())
            .stockQuantity(400L)
            .build();
    var products = Stream.of(rolex, kors, swatch, casio).collect(Collectors.toList());
    return productRepository.saveAll(products);
  }

  @Transactional
  User createUser() {
    var user = User.builder().username("foo").password("AAaasd1234@@").build();

    var su = userRepository.save(user);
    cartRepository.save(Cart.builder().cartStatus(CartStatus.ACTIVE).user(su).build());
    return user;
  }
}
