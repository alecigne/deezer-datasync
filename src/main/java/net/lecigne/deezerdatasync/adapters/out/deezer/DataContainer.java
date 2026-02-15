package net.lecigne.deezerdatasync.adapters.out.deezer;

import java.util.List;

interface DataContainer<T> {
  List<T> getData();
  int getTotal();
}
