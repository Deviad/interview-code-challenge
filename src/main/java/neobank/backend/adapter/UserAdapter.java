package neobank.backend.adapter;

import neobank.backend.domain.aggregate.UserAggregate;
import neobank.backend.persistence.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ValueObjectMapper.class)
public interface UserAdapter {

  UserAggregate entityToAggregate(User user);

  User aggregateToEntity(UserAggregate aggregate);
}
