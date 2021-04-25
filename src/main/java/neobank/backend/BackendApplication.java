package neobank.backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.sql.SQLException;

@SpringBootApplication
// @EnableJpaRepositories(basePackages = "neobank.backend.persistence")
// @EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class BackendApplication {

  public static void main(String[] args) throws SQLException {

    // Allow auto-creating new databases on disk at first connection

    org.h2.tools.Server.createTcpServer("-ifNotExists").start();

    new SpringApplicationBuilder(BackendApplication.class)
        .profiles(
            "spa") // re-enables WEB nature (disabled in application.properties for the other apps)
        .run(args);
  }
}
