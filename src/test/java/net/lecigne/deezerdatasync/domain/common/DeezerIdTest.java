package net.lecigne.deezerdatasync.domain.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("A Deezer ID")
class DeezerIdTest {

  @Test
  void should_not_be_instantiated_with_incorrect_value() {
    assertThatThrownBy(() -> new DeezerId(-1))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Deezer ID must be a valid number");
  }

}
