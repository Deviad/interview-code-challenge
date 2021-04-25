package neobank.backend.adapter;

import neobank.backend.domain.aggregate.ProductAggregate;
import neobank.backend.persistence.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface ProductAdapter {

  ProductAggregate entityToAggregate(Product product);

  Product aggregateToEntity(ProductAggregate productAggregate);

  List<Product> aggregateListToEntity(List<ProductAggregate> productAggregate);
}
