package neobank.backend.domain.aggregate;

import neobank.backend.domain.valueobject.CartId;
import neobank.backend.domain.valueobject.CartQuantity;
import neobank.backend.domain.valueobject.CartStatus;
import neobank.backend.domain.valueobject.NumberOfFreeItems;
import neobank.backend.domain.valueobject.MinItemsForDiscount;
import neobank.backend.domain.valueobject.Password;
import neobank.backend.domain.valueobject.UnitPrice;
import neobank.backend.domain.valueobject.ProductId;
import neobank.backend.domain.valueobject.ProductName;
import neobank.backend.domain.valueobject.Quantity;
import neobank.backend.domain.valueobject.SKU;
import neobank.backend.domain.valueobject.UserId;
import neobank.backend.domain.valueobject.Username;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CartAggregateTest {
    public static List<ProductAggregate> products = new ArrayList<>();

    @BeforeEach
    void setUp() {
    }

    @BeforeAll
    static void fixture() {
        var skuId = UUID.randomUUID();
        var product = ProductAggregate.builder()
                .id(new ProductId(1L))
                .productName(new ProductName("Product Name"))
                .unitPrice(new UnitPrice(100D))
                .stockQuantity(new Quantity(5000L))
                .minItemsForDiscount(new MinItemsForDiscount(200))
                .numberOfFreeItems(new NumberOfFreeItems(3))
                .sku(new SKU(skuId))
                .build();
        products.add(product);
    }


    @Test
    void should_create_cart_successfully() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        assertThat(c.cartStatus()).isEqualTo(CartStatus.ACTIVE);
        assertThat(c.cartId()).isEqualTo(new CartId(1L));
        assertThat(c.userAggregate().getId()).isEqualTo(new UserId(1L));
    }


    @Test
    void should_calculate_total_number_of_products_groupedby_sku_correctly() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        c.changeProductQuantity(products.get(0), new CartQuantity(1L));
        assertThat(c.calculateTotalFinalPrice()).isEqualTo(100D);
        c.changeProductQuantity(products.get(0), new CartQuantity(200L));
        assertThat(Math.round(c.calculateTotalFinalPrice()*100/100.0)).isEqualTo(Math.round(19700));

    }

    @Test
    void should_removeProductFromCart_successfully() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        c.changeProductQuantity(products.get(0), new CartQuantity(0L));
        assertThat(c.showCartProducts().size()).isEqualTo(0);
    }

    @Test
    void should_throw_error_when_removing_product_from_empty_cart() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));

        assertThatThrownBy(() -> c.changeProductQuantity(products.get(0), new CartQuantity(-1L)))
                .isInstanceOf(RuntimeException.class);

    }

    @Test
    void should_remove_all_items_at_once_when_empty_cart_is_called() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        c.changeProductQuantity(products.get(0), new CartQuantity(20L));
        c.emptyCart();
        assertThat(c.showCartProducts().size()).isEqualTo(0);
    }

    @Test
    void should_showCartProducts() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        c.changeProductQuantity(products.get(0), new CartQuantity(1L));
        assertThat(c.showCartProducts()).isEqualTo(Map.of(products.get(0), new CartQuantity(1L)));

    }

    @Test
    void should_checkout_the_cart() {
        var c = CartAggregate.createCart(
                new CartId(1L),
                new UserAggregate(new UserId(1L), new Username("hello"), new Password("WorldA111@")));
        assertThatThrownBy(c::checkout).isInstanceOf(RuntimeException.class);
        c.changeProductQuantity(products.get(0), new CartQuantity(1L));
        c.checkout();
        assertThat(c.cartStatus()).isEqualTo(CartStatus.CHECKED_OUT);
    }
}
