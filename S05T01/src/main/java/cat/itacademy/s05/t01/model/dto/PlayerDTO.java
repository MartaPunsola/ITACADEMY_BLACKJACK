package cat.itacademy.s05.t01.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "A player's basic info.")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {

    @Schema(description = "A player's unique ID.", example = "1")
    private Long id;
    @Schema(description = "A player's username.", example = "Ramona")
    private String username;
    @Schema(description = "The total number of games played by a player.", example = "10")
    private int gamesPlayed;
    @Schema(description = "The total number of games won by a player.", example = "5")
    private int gamesWon;

    public PlayerDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
