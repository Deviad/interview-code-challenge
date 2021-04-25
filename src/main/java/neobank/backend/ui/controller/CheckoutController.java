package neobank.backend.ui.controller;

import lombok.RequiredArgsConstructor;
import neobank.backend.ui.command.AddProduct;
import neobank.backend.ui.command.Checkout;
import neobank.backend.ui.command.CommandHandler;
import neobank.backend.ui.query.CartQueryService;
import neobank.backend.ui.response.CartWithFinalPrice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class CheckoutController {

  private final CommandHandler handler;
  private final CartQueryService queryService;

  @PostMapping(
      value = "/cart/checkout",
      consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE},
      produces = APPLICATION_JSON_VALUE)
  void checkout(@RequestBody Checkout command) {
    handler.handleCommand(command);
  }

  @PostMapping(
      value = "/cart/changequantity",
      consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE},
      produces = APPLICATION_JSON_VALUE)
  void addProduct(@RequestBody AddProduct command) {
    handler.handleCommand(command);
  }

  @GetMapping(value = "/cart/{username}/getcartinfo", produces = APPLICATION_JSON_VALUE)
  CartWithFinalPrice getCartInfo(@PathVariable String username) {
    return queryService.getCartWithFinalPrice(username);
  }
}
