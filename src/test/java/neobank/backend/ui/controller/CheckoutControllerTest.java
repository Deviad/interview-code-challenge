package neobank.backend.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import neobank.backend.infrastructure.ControllerExceptionHandler;
import neobank.backend.ui.command.CommandHandler;
import neobank.backend.ui.query.CartQueryService;
import neobank.backend.ui.response.CartWithFinalPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(CheckoutController.class)
@ContextConfiguration(classes={ControllerExceptionHandler.class, CheckoutController.class})
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @MockBean
    private CommandHandler handler;

    @MockBean
    private CartQueryService queryService;

    @InjectMocks
    private CheckoutController controller;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }


    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideCarts")
    @DisplayName("When the endpoint is called it should return cart info")
    public void getCartInfo(
            String username,
            Long productId,
            String sku,
            Integer minItems,
            Integer numOfFreeItems,
            Long quantity,
            Long stockQuantity,
            Double unitPrice,
            String productName,
            Long userId,
            Long cartId,
            String cartStatus,
            Double totalFinalPrice,
            Object exception


        ) {

        var product = CartWithFinalPrice.Product.builder()
                .productId(productId)
                .productSku(sku)
                .minItemsForDiscount(minItems)
                .numberOfFreeItems(numOfFreeItems)
                .quantity(quantity)
                .stockQuantity(stockQuantity)
                .unitPrice(unitPrice)
                .productName(productName)
                .build();

        var cart = CartWithFinalPrice.builder()
                .userId(userId)
                .cartId(cartId)
                .cartStatus(cartStatus)
                .products(Collections.singletonList(product))
                .totalFinalPrice(totalFinalPrice)
                .build();


        lenient().when(queryService.getCartWithFinalPrice(ArgumentMatchers.eq("foo"))).thenReturn(cart);
        lenient().when(queryService.getCartWithFinalPrice(ArgumentMatchers.eq("bar"))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get(String.format("/cart/%s/getcartinfo", username)))
                .andExpect(exception == null ? status().isOk(): result ->
                                                                     assertThat(result.getResolvedException()
                                                                    .toString().equals(exception.getClass().getName())))
                .andExpect(exception == null ? content()
                                               .string(mapper.writeValueAsString(cart)) : content()
                                                                                          .string(""));
    }


    public static Iterable<Object[]> provideCarts() {
        return Arrays.asList(new Object[][] {
                {"foo", 1L, "1409ca7a-1b11-43f0-8c0a-336386b416b1", 200, 2, 201L, 1L, 100.0, "Hello", 100L, 200L, "LIVE", 2000.0, null},
                {"bar", 12L, "fadaaa0c-2b90-49b4-88d6-5c192130c6d6", 200, 2, 201L, 1L, 100.0, "Hello", 100L, 200L, "LIVE", 2000.0, EntityNotFoundException.class}});
        }
    }
