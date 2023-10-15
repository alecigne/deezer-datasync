package net.lecigne.deezerdatasync;

import static net.lecigne.deezerdatasync.config.DeezerDatasyncConfig.ROOT_CONFIG;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import net.lecigne.deezerdatasync.business.DeezerDatasyncBusiness;
import net.lecigne.deezerdatasync.config.DeezerDatasyncConfig;

@Slf4j
public class DeezerDatasync {

  public static void main(String[] args) {
    Config typeSafeConfig = ConfigFactory.load();
    DeezerDatasyncConfig config = ConfigBeanFactory.create(typeSafeConfig.getConfig(ROOT_CONFIG), DeezerDatasyncConfig.class);
    DeezerDatasyncBusiness.init(config).sync();
  }

}
