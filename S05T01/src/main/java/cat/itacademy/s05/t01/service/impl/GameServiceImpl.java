package cat.itacademy.s05.t01.service.impl;

import cat.itacademy.s05.t01.exception.custom.GameNotFoundException;
import cat.itacademy.s05.t01.service.GameLogicService;
import cat.itacademy.s05.t01.model.Game;
import cat.itacademy.s05.t01.model.dto.GameDTO;
import cat.itacademy.s05.t01.model.enums.Move;
import cat.itacademy.s05.t01.model.player.PlayerPlaying;
import cat.itacademy.s05.t01.repository.GameRepository;
import cat.itacademy.s05.t01.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerServiceImpl playerService;
    @Autowired
    private GameLogicService gameLogicService;


    @Override
    public Mono<GameDTO> newGame(String playerName) {
        return gameLogicService.initiateGame()
                .flatMap(game -> addPlayer(game, playerName))
                .flatMap(gameRepository::save)
                .map(savedGame -> new GameDTO(savedGame.getId(), savedGame.getPlayer().getUsername()));
    }

    @Override
    public Mono<Game> addPlayer(Game game, String playerName) {
        return playerService.getPlayerByName(playerName)
                .switchIfEmpty(playerService.createPlayer(playerName))
                .flatMap(player -> {
                    PlayerPlaying newPlayer = new PlayerPlaying(player.getId(), player.getUsername());
                    game.setPlayer(newPlayer);
                    return Mono.just(game);
                });
    }

    @Override
    public Mono<GameDTO> getGamebyId(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with id " + id + " not found.")))
                .map(foundGame -> new GameDTO(foundGame.getId(), foundGame.getPlayer().getUsername(), foundGame.getStatus()));
    }

    @Override
    public Mono<GameDTO> play(String id, Move move, int bet) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with id " + id + " not found.")))
                .flatMap(game -> gameLogicService.play(game, move, bet))
                .flatMap(game -> gameLogicService.croupierMakesMove(game))
                .flatMap(game -> gameLogicService.determineFinalStatus(game))
                .flatMap(gameRepository::save)
                .flatMap(game -> playerService.addPlayedGame(game.getPlayer().getId())
                        .thenReturn(game))
                .map(updatedGame -> new GameDTO(updatedGame.getId(), updatedGame.getPlayer().getUsername(), updatedGame.getStatus(), updatedGame.getPlayer().getStatus()));
    }

    @Override
    public Mono<Void> deleteGame(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game with id " + id + " not found.")))
                .flatMap(g -> gameRepository.deleteById(id));
    }

}
