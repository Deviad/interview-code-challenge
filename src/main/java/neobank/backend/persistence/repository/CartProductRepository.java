package neobank.backend.persistence.repository;

import neobank.backend.persistence.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

  List<CartProduct> findAllByCartId(long id);

  List<CartProduct> findAllByIdIn(List<Long> ids);
}
