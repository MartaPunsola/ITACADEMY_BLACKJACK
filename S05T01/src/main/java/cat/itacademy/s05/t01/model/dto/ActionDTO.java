package cat.itacademy.s05.t01.model.dto;

import cat.itacademy.s05.t01.model.enums.Move;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "The content of a player's actions.")
@Getter
public class ActionDTO {

    @Schema(description = "A player's move: hit or stand.", example = "HIT")
    private Move move;
    @Schema(description = "The quantity of the bet.", example = "50")
    private int bet;
}
