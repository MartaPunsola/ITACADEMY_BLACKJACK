package cat.itacademy.s05.t01.service;

import cat.itacademy.s05.t01.exception.custom.NoPlayersException;
import cat.itacademy.s05.t01.exception.custom.PlayerNotFoundException;
import cat.itacademy.s05.t01.model.dto.PlayerDTO;
import cat.itacademy.s05.t01.model.player.Player;
import cat.itacademy.s05.t01.repository.PlayerRepository;
import cat.itacademy.s05.t01.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @InjectMocks
    private PlayerServiceImpl playerServiceMock;

    @Mock
    private PlayerRepository playerRepository;

    @DisplayName("Testing the PlayerServiceImpl method to create a new player.")
    @Test
    public void createPlayerTest() {
        String playerName = "Adefesio";
        Player mockPlayer = new Player(playerName);
        mockPlayer.setId(1L);
        
        when(playerRepository.save(any(Player.class)))
                .thenReturn(Mono.just(mockPlayer));

        Mono<PlayerDTO> result = playerServiceMock.createPlayer(playerName);
        
        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(1L, playerDTO.getId());
                    assertEquals("Adefesio", playerDTO.getUsername());
                    return true;
                })
                .verifyComplete();  

        verify(playerRepository).save(argThat(player -> player.getUsername().equals(playerName)));
    }

    @DisplayName("Testing the PlayerServiceImpl method to find a player by ID (player found).")
    @Test
    public void getPlayerByIdTest_shouldReturnPlayerWhenFound() {
        Long playerId = 1L;
        Player mockPlayer = new Player("Pepi");
        mockPlayer.setId(playerId);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(mockPlayer));

        Mono<Player> result = playerServiceMock.getPlayerById(playerId);
        
        StepVerifier.create(result)
                .expectNextMatches(player -> {
                    assertEquals(playerId, player.getId());
                    assertEquals("Pepi", player.getUsername());
                    return true;
                })
                .verifyComplete();

        verify(playerRepository).findById(playerId);
    }

    @DisplayName("Testing the PlayerServiceImpl method to find a player by ID (player not found).")
    @Test
    public void getPlayerByIdTest_shouldThrowExceptionWhenNotFound() {
        Long playerId = 1L;

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.empty());
        
        Mono<Player> result = playerServiceMock.getPlayerById(playerId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PlayerNotFoundException &&
                        throwable.getMessage().equals("Player with id " + playerId + " not found."))
                .verify();
        
        verify(playerRepository).findById(playerId);
    }

    @DisplayName("Testing the PlayerServiceImpl method to find a player by name.")
    @Test
    public void getPlayerByNameTest() {
        String playerName = "Ramona";
        Player mockPlayer = new Player(playerName);
        mockPlayer.setId(1L);

        when(playerRepository.getPlayerByName(playerName))
                .thenReturn(Mono.just(mockPlayer));
        
        Mono<PlayerDTO> result = playerServiceMock.getPlayerByName(playerName);

        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(1L, playerDTO.getId());
                    assertEquals("Ramona", playerDTO.getUsername());
                    return true;
                })
                .verifyComplete();

        verify(playerRepository).getPlayerByName(playerName);
    }

    @DisplayName("Testing the PlayerServiceImpl method to rank the players.") 
    @Test
    public void getPlayersRankingTest_shouldReturnSortedPlayers() {
        Player player1 = new Player("Dandi");
        player1.setId(1L);
        player1.setGamesPlayed(15);
        player1.setGamesWon(10);

        Player player2 = new Player("Mimi");
        player2.setId(2L);
        player2.setGamesPlayed(12);
        player2.setGamesWon(5);

        List<Player> sortedPlayers = List.of(player1, player2);

        when(playerRepository.findAllSortedByGamesWon())
                .thenReturn(Flux.fromIterable(sortedPlayers));

        Mono<List<PlayerDTO>> result = playerServiceMock.getPlayersRanking();

        StepVerifier.create(result)
                .expectNextMatches(playerDTOs -> {
                    assertEquals(2, playerDTOs.size());

                    PlayerDTO dto1 = playerDTOs.get(0);
                    assertEquals(1L, dto1.getId());
                    assertEquals("Dandi", dto1.getUsername());
                    assertEquals(15, dto1.getGamesPlayed());
                    assertEquals(10, dto1.getGamesWon());

                    PlayerDTO dto2 = playerDTOs.get(1);
                    assertEquals(2L, dto2.getId());
                    assertEquals("Mimi", dto2.getUsername());
                    assertEquals(12, dto2.getGamesPlayed());
                    assertEquals(5, dto2.getGamesWon());

                    return true;
                })
                .verifyComplete();

        verify(playerRepository).findAllSortedByGamesWon();
    }

    @DisplayName("Testing the PlayerServiceImpl method to rank the players when the players database is empty.") 
    @Test
    public void getPlayersRankingTest_shouldThrowNoPlayersExceptionWhenNoPlayers() {
        when(playerRepository.findAllSortedByGamesWon())
                .thenReturn(Flux.empty());

        Mono<List<PlayerDTO>> result = playerServiceMock.getPlayersRanking();
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NoPlayersException &&
                        throwable.getMessage().equals("There are no players registered.")
                )
                .verify();
        
        verify(playerRepository).findAllSortedByGamesWon();
    }

    @DisplayName("Testing the PlayerServiceImpl methods to update a player's balance.")
    @Test
    public void updateBalanceTest() {
        Long playerId = 1L;
        int bet = 50;
        Player mockPlayer = new Player("Rory");
        mockPlayer.setId(playerId);
        mockPlayer.setBalance(100);

        Player updatedPlayer = new Player("Rory");
        updatedPlayer.setId(playerId);
        updatedPlayer.setBalance(200);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(mockPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> {
                    Player savedPlayer = invocation.getArgument(0);
                    assertEquals(200, savedPlayer.getBalance());
                    return Mono.just(savedPlayer);
                });

        Mono<PlayerDTO> result = playerServiceMock.addToBalance(playerId, bet);
        
        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(playerId, playerDTO.getId());
                    return true;
                })
                .verifyComplete();
        
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
    }

    @DisplayName("Testing the PlayerServiceImpl method to add a game to a player.")
    @Test
    public void addPlayedGameTest() {
        Long playerId = 1L;
        Player mockPlayer = new Player("Encarna");
        mockPlayer.setId(playerId);
        mockPlayer.setGamesPlayed(5);

        Player updatedPlayer = new Player("Encarna");
        updatedPlayer.setId(playerId);
        updatedPlayer.setGamesPlayed(6);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(mockPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> {
                    Player savedPlayer = invocation.getArgument(0);
                    assertEquals(6, savedPlayer.getGamesPlayed());
                    return Mono.just(savedPlayer);
                });

        Mono<PlayerDTO> result = playerServiceMock.addPlayedGame(playerId);
        
        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(playerId, playerDTO.getId());
                    return true;
                })
                .verifyComplete();
        
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
    }

    @DisplayName("Testing the PlayerServiceImpl method to add a successful game to a player.")
    @Test
    public void addWonGameTest() {
        Long playerId = 1L;
        Player mockPlayer = new Player("Antonia");
        mockPlayer.setId(playerId);
        mockPlayer.setGamesWon(3);

        Player updatedPlayer = new Player("Antonia");
        updatedPlayer.setId(playerId);
        updatedPlayer.setGamesWon(4);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(mockPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> {
                    Player savedPlayer = invocation.getArgument(0);
                    assertEquals(4, savedPlayer.getGamesWon());
                    return Mono.just(savedPlayer);
                });

        Mono<PlayerDTO> result = playerServiceMock.addWonGame(playerId);
        
        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(playerId, playerDTO.getId());
                    return true;
                })
                .verifyComplete();

        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
    }

    @DisplayName("Testing the PlayerServiceImpl method to update a player's name.")
    @Test
    public void updatePlayerNameTest() {
        Long playerId = 1L;
        String newPlayerName = "UpdatedName";
        Player mockPlayer = new Player("OriginalName");
        mockPlayer.setId(playerId);

        Player updatedPlayer = new Player(newPlayerName);
        updatedPlayer.setId(playerId);

        when(playerRepository.findById(playerId))
                .thenReturn(Mono.just(mockPlayer));
        when(playerRepository.save(any(Player.class)))
                .thenAnswer(invocation -> {
                    Player savedPlayer = invocation.getArgument(0);
                    assertEquals(newPlayerName, savedPlayer.getUsername());
                    return Mono.just(savedPlayer);
                });

        Mono<PlayerDTO> result = playerServiceMock.updatePlayerName(playerId, newPlayerName);

        StepVerifier.create(result)
                .expectNextMatches(playerDTO -> {
                    assertEquals(playerId, playerDTO.getId());
                    assertEquals(newPlayerName, playerDTO.getUsername());
                    return true;
                })
                .verifyComplete();

        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
    }

}
