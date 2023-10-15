package net.lecigne.deezerdatasync.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("The utility class")
class DeezerDatasyncUtilsTest {

  @ParameterizedTest
  @MethodSource("normalData")
  void should_compute_right_number_of_remaining_calls(int total, int limit, int expected) {
    // When
    int numberOfCalls = DeezerDatasyncUtils.computeRemainingCalls(total, limit);

    // Then
    assertThat(numberOfCalls).isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("outliers")
  @SuppressWarnings("ResultOfMethodCallIgnored")
  void should_not_accept_outliers(int total, int limit) {
    // "When"
    ThrowingCallable throwingCallable = () -> DeezerDatasyncUtils.computeRemainingCalls(total, limit);

    // Then
    assertThatThrownBy(throwingCallable)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Limit must be greater than zero.");
  }

  public static Stream<Arguments> normalData() {
    return Stream.of(
        arguments(250, 100, 2),
        arguments(150, 100, 1),
        arguments(100, 100, 0),
        arguments(50, 100, 0),
        arguments(0, 100, 0),
        arguments(-50, 100, 0),
        arguments(150, 1, 149),
        arguments(150, 4, 37)
    );
  }

  public static Stream<Arguments> outliers() {
    return Stream.of(arguments(100, 0), arguments(100, -2));
  }

}