package neobank.backend.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import neobank.backend.domain.valueobject.CartStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "carts")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  Long id;

  @OneToMany(mappedBy = "cart")
  private Set<CartProduct> cartProducts = new HashSet<>();

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "cart_status")
  @Enumerated(EnumType.STRING)
  private CartStatus cartStatus;

  @Column(name = "total_final_price")
  private double totalFinalPrice;
}
