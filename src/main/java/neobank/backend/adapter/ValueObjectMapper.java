package neobank.backend.adapter;

import neobank.backend.domain.valueobject.ValueObject;
import neobank.backend.infrastructure.WrappedException;
import org.mapstruct.TargetType;

import java.lang.reflect.InvocationTargetException;

public interface ValueObjectMapper {

  static <V extends ValueObject<T>, T> T mapToValue(V valueObject) {
    return valueObject == null ? null : valueObject.getValue();
  }

  static <V extends ValueObject<T>, T> V mapFromValueObject(
      T value, @TargetType Class<V> valueObjectClass) {
    if (value == null) {
      return null;
    }

    try {

      return valueObjectClass.getDeclaredConstructor(value.getClass()).newInstance(value);
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | IllegalArgumentException
        | NoSuchMethodException e) {
      throw new WrappedException(e.getMessage(), e.getCause());
    }
  }
}
