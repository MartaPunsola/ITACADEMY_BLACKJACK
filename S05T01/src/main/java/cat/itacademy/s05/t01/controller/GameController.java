package cat.itacademy.s05.t01.controller;

import cat.itacademy.s05.t01.model.dto.ActionDTO;
import cat.itacademy.s05.t01.service.GameService;
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

import java.net.URI;

@RestController
@RequestMapping("/game")
@Tag(name = "Game controller", description = "Endpoints related to game management")
public class GameController {

    @Autowired
    private GameService gameService;

    @Operation(
            summary = "Game creation",
            description = "It creates a new game with a specified player.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The player's username",
                    required = true
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/new")
    public Mono<ResponseEntity<String>> newGame(@RequestBody String playerName) {
        return gameService.newGame(playerName)
                .map(newGame -> ResponseEntity.created(URI.create("/game/" + newGame.getId())).body("New game successfully created. Game id: " + newGame.getId() + ", Player: " + newGame.getPlayerUsername() + "."))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(
            summary = "Get details of a game",
            description = "It shows the game ID, the player username and the game status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid id"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<String>> getGame(@PathVariable String id) {
        return gameService.getGamebyId(id)
                .map(foundGame -> ResponseEntity.status(HttpStatus.OK).body("Game id: " + foundGame.getId() + ", Player: " + foundGame.getPlayerUsername() + ", Game status: " + foundGame.getGameStatus() + "."))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(
            summary = "Play",
            description = "It makes a move and concludes the game.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A player's move and bet",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActionDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @PostMapping("/{id}/play")
    public Mono<ResponseEntity<String>> play(@PathVariable String id, @RequestBody ActionDTO action) {
        return gameService.play(id, action.getMove(), action.getBet())
                .map(game -> ResponseEntity.status(HttpStatus.OK).body("Game id: " + game.getId() + ", Player: " + game.getPlayerUsername() + ", Game status: " + game.getGameStatus() + ", Player status: " + game.getPlayerFinalStatus() + "."))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(
            summary = "Delete a game",
            description = "It deletes a game found by ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<String>> deleteGameById(@PathVariable String id) {
        return gameService.deleteGame(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

}
