package neobank.backend.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import neobank.backend.domain.valueobject.ProductType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
    name = "products",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"sku"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @EqualsAndHashCode.Include
  @ToString.Include
  Long id;

  @Column(name = "product_name")
  String productName;

  @Enumerated(EnumType.ORDINAL)
  ProductType productType = ProductType.WATCH;

  @Column(name = "unit_price")
  double unitPrice;

  @Column(name = "stock_quantity")
  long stockQuantity;

  @Column(name = "min_items_for_discount")
  int minItemsForDiscount;

  int numberOfFreeItems;

  @OneToMany(mappedBy = "product")
  private Set<CartProduct> cartProducts = new HashSet<>();

  @Column(nullable = false, unique = true)
  @Type(type = "uuid-char")
  UUID sku;
}
