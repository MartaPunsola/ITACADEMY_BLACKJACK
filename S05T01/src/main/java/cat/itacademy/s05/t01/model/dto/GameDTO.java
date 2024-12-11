package cat.itacademy.s05.t01.model.dto;

import cat.itacademy.s05.t01.model.enums.PlayerStatus;
import cat.itacademy.s05.t01.model.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {

    private String id;
    private String playerUsername;
    private GameStatus gameStatus;
    private PlayerStatus playerFinalStatus;

    public GameDTO(String id, String playerUsername) {
        this.id = id;
        this.playerUsername = playerUsername;
    }

    public GameDTO(String id, String playerUsername, GameStatus gameStatus) {
        this.id = id;
        this.playerUsername = playerUsername;
        this.gameStatus = gameStatus;
    }
}
