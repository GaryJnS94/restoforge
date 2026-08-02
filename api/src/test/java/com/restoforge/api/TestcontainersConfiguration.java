package com.restoforge.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  /**
   * Démarre un conteneur PostgreSQL dédié aux tests.
   * L'image doit rester alignée sur celle de compose.yaml : tester contre
   * une version différente de celle utilisée en dev reviendrait à valider
   * un moteur qu'on n'exécute nulle part.
   *
   * @ServiceConnection extrait l'URL réelle du conteneur (le port est
   *                    attribué dynamiquement par Docker) et l'injecte dans la
   *                    configuration
   *                    Spring avant la construction de la datasource.
   */
  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>("postgres:16-alpine");
  }
}