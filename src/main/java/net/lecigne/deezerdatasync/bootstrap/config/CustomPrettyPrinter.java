package net.lecigne.deezerdatasync.bootstrap.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import java.io.IOException;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {

  public CustomPrettyPrinter() {
    DefaultIndenter indenter = new DefaultIndenter().withLinefeed("\n");
    _arrayIndenter = indenter;
    _objectIndenter = indenter;
  }

  @Override
  public DefaultPrettyPrinter createInstance() {
    return new CustomPrettyPrinter();
  }

  @Override
  public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
    g.writeRaw(": ");
  }

}
