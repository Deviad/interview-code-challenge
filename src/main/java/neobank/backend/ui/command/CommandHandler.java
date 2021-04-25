package neobank.backend.ui.command;

import lombok.RequiredArgsConstructor;
import neobank.backend.adapter.AddProductAdapter;
import neobank.backend.application.commandservice.CartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandHandler {

  private final CartService cartService;
  private final AddProductAdapter addProductAdapter;

  public void handleCommand(Checkout checkout) {
    cartService.checkout(checkout.getUsername());
  }

  public void handleCommand(AddProduct command) {

    cartService.updateCart(
        command.getUsername(), deserializedAddProductCommand(command).getProducts());
  }

  private AddProductDes deserializedAddProductCommand(AddProduct command) {
    return addProductAdapter.map(command);
  }
}
