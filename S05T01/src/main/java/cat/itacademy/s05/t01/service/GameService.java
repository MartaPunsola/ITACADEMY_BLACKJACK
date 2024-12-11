package cat.itacademy.s05.t01.service;

import cat.itacademy.s05.t01.model.Game;
import cat.itacademy.s05.t01.model.dto.GameDTO;
import cat.itacademy.s05.t01.model.enums.Move;
import reactor.core.publisher.Mono;

public interface GameService {

    Mono<GameDTO> newGame(String playerName);
    Mono<Game> addPlayer(Game game, String playerName);
    Mono<GameDTO> getGamebyId(String id);
    Mono<GameDTO> play(String id, Move move, int bet);
    Mono<Void> deleteGame(String id);
}
