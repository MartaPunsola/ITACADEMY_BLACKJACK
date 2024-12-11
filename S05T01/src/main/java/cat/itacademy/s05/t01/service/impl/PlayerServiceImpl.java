package cat.itacademy.s05.t01.service.impl;

import cat.itacademy.s05.t01.exception.custom.NoPlayersException;
import cat.itacademy.s05.t01.exception.custom.PlayerNotFoundException;
import cat.itacademy.s05.t01.model.dto.PlayerDTO;
import cat.itacademy.s05.t01.model.player.Player;
import cat.itacademy.s05.t01.repository.PlayerRepository;
import cat.itacademy.s05.t01.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;


    @Override
    public Mono<PlayerDTO> createPlayer(String playerName) {
        return playerRepository.save(new Player(playerName))
                .map(savedPlayer -> new PlayerDTO(savedPlayer.getId(), savedPlayer.getUsername()));
    }

    @Override
    public Mono<Player> getPlayerById(Long id) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with id " + id + " not found.")));
    }

    @Override
    public Mono<PlayerDTO> getPlayerByName(String name) {
        return playerRepository.getPlayerByName(name)
                .map(foundPlayer -> new PlayerDTO(foundPlayer.getId(), foundPlayer.getUsername()));
    }

    @Override
    public Mono<List<PlayerDTO>> getPlayersRanking() {
        return playerRepository.findAllSortedByGamesWon()
                .switchIfEmpty(Mono.error(new NoPlayersException("There are no players registered.")))
                .map(player -> new PlayerDTO(player.getId(), player.getUsername(), player.getGamesPlayed(), player.getGamesWon()))
                .collectList();
    }

    @Override
    public Mono<PlayerDTO> addToBalance(Long playerId, int bet) {
        return getPlayerById(playerId)
                .flatMap(player -> {
                    player.setBalance(player.getBalance() + (bet * 2));
                    return playerRepository.save(player);
                })
                .map(updatedPlayer -> new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getUsername()));
    }

    @Override
    public Mono<PlayerDTO> subtractFromBalance(Long playerId, int bet) {
        return getPlayerById(playerId)
                .flatMap(player -> {
                    player.setBalance(player.getBalance() - bet);
                    return playerRepository.save(player);
                })
                .map(updatedPlayer -> new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getUsername()));
    }

    @Override
    public Mono<PlayerDTO> addPlayedGame(Long playerId) {
        return getPlayerById(playerId)
                .flatMap(player -> {
                    player.setGamesPlayed(player.getGamesPlayed() + 1);
                    return playerRepository.save(player);
                })
                .map(updatedPlayer -> new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getUsername()));
    }

    @Override
    public Mono<PlayerDTO> addWonGame(Long playerId) {
        return getPlayerById(playerId)
                .flatMap(player -> {
                    player.setGamesWon(player.getGamesWon() + 1);
                    return playerRepository.save(player);
                })
                .map(updatedPlayer -> new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getUsername()));
    }

    @Override
    public Mono<PlayerDTO> updatePlayerName(Long playerId, String playerName) {
        return getPlayerById(playerId)
                .flatMap(player -> {
                    player.setUsername(playerName);
                    return playerRepository.save(player);
                })
                .map(updatedPlayer -> new PlayerDTO(updatedPlayer.getId(), updatedPlayer.getUsername()));
    }

}
