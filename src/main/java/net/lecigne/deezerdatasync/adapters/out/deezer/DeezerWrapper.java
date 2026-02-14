package net.lecigne.deezerdatasync.adapters.out.deezer;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
class DeezerWrapper<T> implements DataContainer<T> {
  List<T> data;
  int total;
}
