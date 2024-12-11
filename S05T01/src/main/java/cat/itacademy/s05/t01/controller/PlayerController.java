package cat.itacademy.s05.t01.controller;

import cat.itacademy.s05.t01.model.dto.PlayerDTO;
import cat.itacademy.s05.t01.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Tag(name = "Player controller", description = "Endpoints related to player management")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Operation(
            summary = "Player's ranking",
            description = "It shows the players sorted by their score of games won"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "There are no registered players",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlayerDTO.class)))
    })
    @GetMapping("/ranking")
    public Mono<ResponseEntity<List<PlayerDTO>>> sortPlayers() {
        return playerService.getPlayersRanking()
                .map(playerDTOs -> ResponseEntity.ok().body(playerDTOs));
    }

    @Operation(
            summary = "Change a player's name",
            description = "It finds a player by ID and changes their name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player's name successfully changed"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @PutMapping("/player/{playerId}")
    public Mono<ResponseEntity<String>> changePlayerName(@PathVariable Long playerId, @RequestBody String playerName) {
        return playerService.updatePlayerName(playerId, playerName)
                .then(Mono.just(ResponseEntity.status(HttpStatus.OK).body("The player's name has been changed to: " + playerName + ".")));
    }

}
