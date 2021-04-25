package neobank.backend.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import neobank.backend.domain.valueobject.Password;
import neobank.backend.domain.valueobject.UserId;
import neobank.backend.domain.valueobject.Username;

@AllArgsConstructor
@Builder
@Data
public class UserAggregate {

  UserId id;
  Username username;
  Password password;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserAggregate)) return false;

    UserAggregate that = (UserAggregate) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
