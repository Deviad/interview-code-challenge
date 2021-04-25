package neobank.backend.ui.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartWithFinalPrice {

  Long userId;
  Long cartId;
  String cartStatus;
  double totalFinalPrice;
  List<Product> products;

  @AllArgsConstructor
  @Builder
  @Getter
  @NoArgsConstructor
  @Setter
  public static class Product {
    Long productId;
    String productSku;
    int minItemsForDiscount;
    int numberOfFreeItems;
    long quantity;
    long stockQuantity;
    double unitPrice;
    private String productName;
  }
}
