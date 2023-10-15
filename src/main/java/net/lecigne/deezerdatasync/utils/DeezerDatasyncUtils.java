package net.lecigne.deezerdatasync.utils;

public final class DeezerDatasyncUtils {

  private DeezerDatasyncUtils() {
  }

  public static int computeRemainingCalls(int total, int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("Limit must be greater than zero.");
    }
    int remainingTotal = total - limit;
    return (remainingTotal + limit - 1) / limit;
  }


}
