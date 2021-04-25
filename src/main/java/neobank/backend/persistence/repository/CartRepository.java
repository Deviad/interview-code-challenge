package neobank.backend.persistence.repository;

import neobank.backend.domain.valueobject.CartStatus;
import neobank.backend.persistence.entity.Cart;
import neobank.backend.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  Cart findCartByUserAndCartStatus(User user, CartStatus cartStatus);

  default Cart findCartByUserIdAndCartStatusActive(User user) {
    return findCartByUserAndCartStatus(user, CartStatus.ACTIVE);
  }
}
