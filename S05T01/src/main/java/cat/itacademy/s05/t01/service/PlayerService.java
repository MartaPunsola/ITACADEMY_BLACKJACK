package cat.itacademy.s05.t01.service;

import cat.itacademy.s05.t01.model.dto.PlayerDTO;
import cat.itacademy.s05.t01.model.player.Player;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PlayerService {

    Mono<PlayerDTO> createPlayer(String playerName);
    Mono<Player> getPlayerById(Long id);
    Mono<PlayerDTO> getPlayerByName(String name);
    Mono<List<PlayerDTO>> getPlayersRanking();
    Mono<PlayerDTO> addToBalance(Long playerId, int bet);
    Mono<PlayerDTO> subtractFromBalance(Long playerId, int bet);
    Mono<PlayerDTO> addPlayedGame(Long playerId);
    Mono<PlayerDTO> addWonGame(Long playerId);
    Mono<PlayerDTO> updatePlayerName(Long id, String playerName);



}
