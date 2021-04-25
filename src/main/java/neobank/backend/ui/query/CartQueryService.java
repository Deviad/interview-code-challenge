package neobank.backend.ui.query;

import com.google.common.base.CaseFormat;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import neobank.backend.infrastructure.MappingUtils;
import neobank.backend.infrastructure.WrappedException;
import neobank.backend.persistence.entity.User;
import neobank.backend.ui.response.CartWithFinalPrice;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartQueryService {
    public static final Supplier<EntityNotFoundException> ENTITY_NOT_FOUND_EXCEPTION_SUPPLIER =
            () -> new EntityNotFoundException("Could not find user with specified username");
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CartWithFinalPrice getCartWithFinalPrice(String username) {

        String cartQuery =
                """
                           select * from CARTS
                           LEFT JOIN CART_PRODUCT ON CARTS.ID = CART_PRODUCT.CART_ID
                           LEFT JOIN PRODUCTS P on CART_PRODUCT.PRODUCT_ID = P.ID
                           WHERE CART_STATUS = 'ACTIVE' AND CARTS.USER_ID = :id
                        """;
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                                                 .addValue("id", user(username).getId());


        return Try.of(() -> jdbcTemplate.queryForList(cartQuery, namedParameters))
                .getOrElseThrow(e -> new WrappedException(e.getMessage(), e.getCause()))
                .stream()
                .collect(Collector.of(CartWithFinalPrice::new, rtBiConsumer(), rBinaryOperator()));
    }

    private BinaryOperator<CartWithFinalPrice> rBinaryOperator() {
        return (result1, result2) -> {
            result1.setProducts(Stream.of(result1.getProducts(), result2.getProducts())
                                       .flatMap(Collection::stream).collect(Collectors.toList()));
            return result1;
        };
    }

    private BiConsumer<CartWithFinalPrice, Map<String, Object>> rtBiConsumer() {
        return (result, row) -> {
            var p = new CartWithFinalPrice.Product();
            result.setCartId((Long) row.get("id"));
            result.setTotalFinalPrice((double) row.get("total_final_price"));
            result.setCartStatus((String) row.get("cart_status"));
            result.setUserId((Long) row.get("user_id"));
            p.setProductId((Long) row.get("product_id"));
            p.setMinItemsForDiscount((int) row.get("min_items_for_discount"));
            p.setNumberOfFreeItems((int) row.get("number_of_free_items"));
            p.setProductName((String) row.get("product_name"));
            p.setQuantity((long) row.get("quantity"));
            p.setProductSku((String) row.get("sku"));
            p.setStockQuantity((long) row.get("stock_quantity"));
            p.setUnitPrice((double) row.get("unit_price"));
            result.setProducts(new ArrayList<>(Collections.singletonList(p)));
        };
    }

    User user(String username) {
        return MappingUtils.MAPPER.convertValue(fetchUserData(username), User.class);
    }

    Map<String, Object> fetchUserData(String username) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("username", username);
        var userQuery = "SELECT u.id, u.username, u.password FROM USERS as u WHERE u.USERNAME = :username";
        return Optional.of(Try.of(() -> jdbcTemplate.queryForMap(userQuery, namedParameters))
                                                     .getOrElseThrow(ENTITY_NOT_FOUND_EXCEPTION_SUPPLIER))
                .orElseThrow(EntityNotFoundException::new)
                .entrySet()
                .stream()
                .map(x -> Map.entry(x.getKey(), x.getValue()))
                .collect(HashMap::new,
                        this::formatKey,
                        Map::putAll);
    }

    private void formatKey(HashMap<String, Object> m, Map.Entry<String, Object> e) {
        m.put(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, e.getKey().toLowerCase()), e.getValue());
    }
}


