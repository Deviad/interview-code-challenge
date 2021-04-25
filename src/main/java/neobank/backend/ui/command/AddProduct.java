package neobank.backend.ui.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddProduct implements Command {
  Map<String, Long> products = new HashMap<>();
  String username;
}
