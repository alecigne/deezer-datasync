package net.lecigne.deezerdatasync.application.ports.in;

import net.lecigne.deezerdatasync.domain.user.UserId;

public interface DeezerDatasync {
  void syncUser(UserId userId);
}
