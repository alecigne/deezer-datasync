package net.lecigne.deezerdatasync.bootstrap;

import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.adapters.in.cli.CLI;
import net.lecigne.deezerdatasync.adapters.out.deezer.DeezerRestClient;
import net.lecigne.deezerdatasync.adapters.out.github.GitHubSyncDestination;
import net.lecigne.deezerdatasync.application.ports.in.DeezerDatasync;
import net.lecigne.deezerdatasync.application.ports.out.DeezerClient;
import net.lecigne.deezerdatasync.application.ports.out.SyncDestination;
import net.lecigne.deezerdatasync.application.services.DeezerDatasyncService;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfig;
import net.lecigne.deezerdatasync.bootstrap.config.DeezerDatasyncConfigLoader;

@Slf4j
public class Application {

  public static void main(String[] args) {
    // Load config
    DeezerDatasyncConfig config = DeezerDatasyncConfigLoader.load();

    // Init adapters/out
    DeezerClient deezerClient = DeezerRestClient.init(config);
    SyncDestination syncDestination = GitHubSyncDestination.init(config);

    // Init application
    DeezerDatasync deezerDatasync = DeezerDatasyncService.init(deezerClient, syncDestination);

    // Init adapters/in
    CLI cli = CLI.init(deezerDatasync);

    // Run
    cli.run(Long.parseLong(config.getAppConfig().getUserId()));
  }

}
