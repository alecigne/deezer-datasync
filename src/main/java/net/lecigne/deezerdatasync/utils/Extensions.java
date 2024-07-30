package net.lecigne.deezerdatasync.utils;

import java.util.List;

public final class Extensions {

  private Extensions() {
  }

  public static <T> List<T> ifEmpty(List<T> sourceList, List<T> otherList) {
    return sourceList == null || sourceList.isEmpty() ? otherList : sourceList;
  }

}
