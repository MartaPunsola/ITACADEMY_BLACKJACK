package cat.itacademy.s05.t01.model.player;

import cat.itacademy.s05.t01.model.enums.PlayerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerPlaying extends Participant {

    private Long id;
    private String username;
    private PlayerStatus status;
    private int initialBet;
    private int gamesWon;

    public PlayerPlaying(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}
