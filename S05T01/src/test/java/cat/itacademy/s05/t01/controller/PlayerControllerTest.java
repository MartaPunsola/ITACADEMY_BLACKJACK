package cat.itacademy.s05.t01.controller;

import cat.itacademy.s05.t01.model.dto.PlayerDTO;
import cat.itacademy.s05.t01.service.PlayerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest {

    @InjectMocks
    private PlayerController playerController;

    @Mock
    private PlayerService mockPlayerService;

    @DisplayName("Testing the PlayerController method to rank players.")
    @Test
    void sortPlayersTest() {
        List<PlayerDTO> playersRanking = List.of(new PlayerDTO(1L, "Pepe"), new PlayerDTO(2L, "Pepita"));

        when(mockPlayerService.getPlayersRanking()).thenReturn(Mono.just(playersRanking));

        ResponseEntity<List<PlayerDTO>> expectedResponse = ResponseEntity.ok(playersRanking);

        StepVerifier.create(playerController.sortPlayers())
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(mockPlayerService).getPlayersRanking();
    }

    @DisplayName("Testing the PlayerController method to update a player's name.")
    @Test
    void changePlayerNameTest() {
        Long playerId = 1L;
        String newName = "Dionisio";

        when(mockPlayerService.updatePlayerName(playerId, newName)).thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = playerController.changePlayerName(playerId, newName);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertEquals("The player's name has been changed to: Dionisio.", response.getBody());
                    return true;
                })
                .verifyComplete();

        verify(mockPlayerService).updatePlayerName(playerId, newName);
    }

}
