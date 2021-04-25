package neobank.backend.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
    name = "cart_product",
    uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class CartProduct {

  @Id
  @EqualsAndHashCode.Include
  @ToString.Include
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne
  @JoinColumn(table = "cart_product", name = "cart_id", nullable = false)
  Cart cart;

  @ManyToOne
  @JoinColumn(table = "cart_product", name = "product_id", nullable = false)
  Product product;

  long quantity;
}
