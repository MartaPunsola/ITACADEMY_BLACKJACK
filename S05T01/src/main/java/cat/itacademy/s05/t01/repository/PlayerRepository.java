package cat.itacademy.s05.t01.repository;

import cat.itacademy.s05.t01.model.player.Player;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends ReactiveCrudRepository<Player, Long> {

    @Query("SELECT * FROM player WHERE lower(username) = lower(:playerName)")
    Mono<Player> getPlayerByName(String playerName);

    @Query("SELECT * FROM player ORDER BY games_Won DESC")
    Flux<Player> findAllSortedByGamesWon();

}
