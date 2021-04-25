package neobank.backend.domain.aggregate;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neobank.backend.domain.valueobject.MinItemsForDiscount;
import neobank.backend.domain.valueobject.NumberOfFreeItems;
import neobank.backend.domain.valueobject.ProductId;
import neobank.backend.domain.valueobject.ProductName;
import neobank.backend.domain.valueobject.ProductType;
import neobank.backend.domain.valueobject.Quantity;
import neobank.backend.domain.valueobject.SKU;
import neobank.backend.domain.valueobject.UnitPrice;

@Builder
@NoArgsConstructor
@Data
public class ProductAggregate {

  ProductId id;
  ProductName productName;
  @Builder.Default ProductType productType = ProductType.WATCH;
  UnitPrice unitPrice;
  Quantity stockQuantity;
  MinItemsForDiscount minItemsForDiscount;
  NumberOfFreeItems numberOfFreeItems;

  SKU sku;

  public ProductAggregate(
      ProductId id,
      ProductName productName,
      ProductType productType,
      UnitPrice unitPrice,
      Quantity stockQuantity,
      MinItemsForDiscount itemsForDiscount,
      NumberOfFreeItems numberOfFreeItems,
      SKU sku) {

    if (id == null) {
      throw new IllegalArgumentException("Product ID cannot be null");
    }
    if (productName == null) {
      throw new IllegalArgumentException("Product name cannot be null");
    }
    if (productType == null) {
      throw new IllegalArgumentException("Product type cannot be null");
    }
    if (itemsForDiscount == null) {
      throw new IllegalArgumentException("N. of items to apply discount cannot be null");
    }
    if (numberOfFreeItems == null) {
      throw new IllegalArgumentException("Please specify the discount to apply to this item");
    }
    if (sku == null) {
      throw new IllegalArgumentException("Please specify the sku for this item");
    }
    if (stockQuantity == null) {
      throw new IllegalArgumentException("Please specify the quantity in stock for this item");
    }

    this.id = id;
    this.productName = productName;
    this.productType = productType;
    this.unitPrice = unitPrice;
    this.stockQuantity = stockQuantity;
    this.minItemsForDiscount = itemsForDiscount;
    this.numberOfFreeItems = numberOfFreeItems;

    this.sku = sku;
  }

  public static ProductAggregate createProduct(
      ProductId id,
      ProductName productName,
      ProductType productType,
      UnitPrice unitPrice,
      Quantity stockQuantity,
      MinItemsForDiscount itemsForDiscount,
      NumberOfFreeItems numberOfFreeItems,
      SKU sku) {
    return new ProductAggregate(
        id,
        productName,
        productType,
        unitPrice,
        stockQuantity,
        itemsForDiscount,
        numberOfFreeItems,
        sku);
  }

  public UnitPrice unitPrice() {
    return unitPrice;
  }

  public ProductAggregate withUnitPrice(UnitPrice unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  public ProductId productId() {
    return id;
  }

  public MinItemsForDiscount minItemsForDiscount() {
    return minItemsForDiscount;
  }

  public ProductAggregate withMinItemsForDiscount(MinItemsForDiscount minItemsForDiscount) {
    this.minItemsForDiscount = minItemsForDiscount;
    return this;
  }

  public NumberOfFreeItems numberOfFreeItems() {
    return numberOfFreeItems;
  }

  public ProductAggregate withNumberOfFreeItems(NumberOfFreeItems numberOfFreeItems) {
    this.numberOfFreeItems = numberOfFreeItems;
    return this;
  }

  public ProductAggregate withId(ProductId productId) {
    this.id = productId;
    return this;
  }

  public ProductName productName() {
    return productName;
  }

  public ProductAggregate withProductName(ProductName productName) {
    this.productName = productName;
    return this;
  }

  public ProductType productType() {
    return productType;
  }

  public ProductAggregate withProductType(ProductType productType) {
    this.productType = productType;
    return this;
  }

  public SKU sku() {
    return sku;
  }

  public ProductAggregate withSku(SKU sku) {
    this.sku = sku;
    return this;
  }

  public Quantity quantity() {
    return stockQuantity;
  }

  public ProductAggregate withStockQuantity(Quantity quantity) {
    if (quantity.getValue() >= 0) {
      this.stockQuantity = quantity;
    } else {
      throw new RuntimeException("Quantity must be greater or equal to 0");
    }
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SKU)) return false;

    SKU that = (SKU) o;

    return sku != null ? sku.getValue().equals(that.getValue()) : that.getValue() == null;
  }

  @Override
  public int hashCode() {
    return sku != null ? sku.hashCode() : 0;
  }
}
