package cat.itacademy.s05.t01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Configuration
public class MySqlDatabaseConfig {

    private final DatabaseClient databaseClient;

    public MySqlDatabaseConfig(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Bean
    public Mono<Void> initializeDatabase() {
        return databaseClient.sql("""
                CREATE SCHEMA IF NOT EXISTS `blackjack_players` DEFAULT CHARACTER SET utf8mb4 ;
                USE `blackjack_players` ;
                CREATE TABLE IF NOT EXISTS `player` (
                  `id` INT NOT NULL AUTO_INCREMENT,
                  `username` VARCHAR(45) NOT NULL,
                  `balance` INT NOT NULL DEFAULT 0,
                  `games_played` INT NOT NULL DEFAULT 0,
                  `games_won` INT NOT NULL DEFAULT 0,
                  PRIMARY KEY (`id`));
                """)
                .then();
    }

}
