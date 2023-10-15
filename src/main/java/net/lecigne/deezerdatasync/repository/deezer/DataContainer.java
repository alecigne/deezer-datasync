package net.lecigne.deezerdatasync.repository.deezer;

import java.util.List;

interface DataContainer<T> {
  List<T> getData();
  int getTotal();
}
