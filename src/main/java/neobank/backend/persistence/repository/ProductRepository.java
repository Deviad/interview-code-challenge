package neobank.backend.persistence.repository;

import neobank.backend.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Product findProductBySku(UUID skuId);

  List<Product> findAllBySkuIn(Set<UUID> skuIds);
}
