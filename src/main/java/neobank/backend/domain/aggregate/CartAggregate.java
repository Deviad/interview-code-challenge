package neobank.backend.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neobank.backend.domain.valueobject.CartId;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.CartStatus;
import neobank.backend.domain.valueobject.TotalFinalPrice;

import java.util.LinkedHashMap;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartAggregate implements Cloneable {

  private CartId id;
  private Map<ProductAggregate, CartQuantity> cartProducts = new LinkedHashMap<>();
  private UserAggregate userAggregate;
  @Builder.Default private CartStatus cartStatus = CartStatus.ACTIVE;
  private TotalFinalPrice totalFinalPrice;

  public CartStatus cartStatus() {
    return cartStatus;
  }

  public CartId cartId() {
    return id;
  }

  public TotalFinalPrice totalFinalPrice() {
    return totalFinalPrice;
  }

  public CartAggregate user(UserAggregate user) {
    this.userAggregate = user;
    return this;
  }

  public UserAggregate userAggregate() {
    return userAggregate;
  }

  public static CartAggregate createCart(CartId cartId, UserAggregate user) {
    return new CartAggregate(cartId, user, CartStatus.ACTIVE);
  }

  public CartAggregate(CartId id, UserAggregate user, CartStatus cartStatus) {
    this.id = id;
    this.userAggregate = user;
    this.cartStatus = cartStatus;
  }

  public CartAggregate changeProductQuantity(ProductAggregate product, CartQuantity quantity) {
    if (quantity.getValue() > 0) {
      cartProducts.put(product, quantity);
    } else if (quantity.getValue() < 0) {
      // In Reality I would be creating new exceptions that extend runtime exception,
      // maybe defining also some codes that can be then mapped on the frontend
      throw new RuntimeException("Quantity must be greater or equal to 0");
    } else {
      cartProducts.remove(product);
    }

    return this;
  }

  public CartAggregate updateFinalPrice(CartQuantity quantity) {
    if (quantity.getValue() >= 0) {
      this.totalFinalPrice = new TotalFinalPrice(calculateTotalFinalPrice());
    } else {
      // In Reality I would be creating new exceptions that extend runtime exception,
      // maybe defining also some codes that can be then mapped on the frontend
      throw new RuntimeException("Quantity must be greater or equal to 0");
    }

    return this;
  }

  CartAggregate emptyCart() {
    if (cartProducts.isEmpty()) {
      // In Reality I would be creating new exceptions that extend runtime exception,
      // maybe defining also some codes that can be then mapped on the frontend
      throw new RuntimeException("Cart is already empty");
    }
    cartProducts.clear();
    return this;
  }

  public Map<ProductAggregate, CartQuantity> showCartProducts() {
    return cartProducts;
  }

  public CartAggregate checkout() {
    if (cartProducts.isEmpty()) {
      // In Reality I would be creating new exceptions that extend runtime exception,
      // maybe defining also some codes that can be then mapped on the frontend
      throw new RuntimeException("Cannot checkout an empty cart");
    }

    cartStatus = CartStatus.CHECKED_OUT;
    return this;
  }

  double calculateTotalFinalPrice() {

    if (cartProducts.isEmpty()) {
      return 0;
    }

    double totalFinalPrice = 0;
    for (var product : cartProducts.entrySet()) {

      final long cartQuantity = product.getValue().getValue();
      final double unitPrice = product.getKey().unitPrice().getValue();
      final double numberOfFreeItems = product.getKey().numberOfFreeItems().getValue();
      final long minItemsForDiscount = product.getKey().minItemsForDiscount().getValue();

      if (numberOfFreeItems == 0 || minItemsForDiscount == 0) {
        totalFinalPrice += (cartQuantity * unitPrice);
        continue;
      }
      if (cartQuantity >= minItemsForDiscount) {
        var nonDiscountedItems = cartQuantity % minItemsForDiscount;
        var discountedItems = cartQuantity - nonDiscountedItems;

        var discountedPrice =
            (cartQuantity * unitPrice)
                - (discountedItems * unitPrice) * (numberOfFreeItems / minItemsForDiscount);
        totalFinalPrice += discountedPrice;
        continue;
      }

      totalFinalPrice += (cartQuantity * unitPrice);
    }

    return totalFinalPrice;
  }

  @Override
  public CartAggregate clone() throws CloneNotSupportedException {
    var clone = (CartAggregate) super.clone();
    clone.setCartProducts(this.getCartProducts());
    clone.setCartStatus(this.getCartStatus());
    clone.setUserAggregate(this.getUserAggregate());
    clone.setId(this.getId());
    clone.setTotalFinalPrice(this.getTotalFinalPrice());
    return clone;
  }
}
