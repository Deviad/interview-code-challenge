package neobank.backend.ui.command;

import lombok.Value;

@Value
public class Checkout implements Command {
  String username;
}
