package net.lecigne.deezerdatasync.utils;

import java.util.List;

public class Extensions {

  public static <T> List<T> ifEmpty(List<T> sourceList, List<T> otherList) {
    return sourceList == null || sourceList.isEmpty() ? otherList : sourceList;
  }

}
