package cat.itacademy.s05.t01.model;

import cat.itacademy.s05.t01.model.card.Card;
import cat.itacademy.s05.t01.model.enums.GameStatus;
import cat.itacademy.s05.t01.model.player.Croupier;
import cat.itacademy.s05.t01.model.player.PlayerPlaying;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "game")
public class Game {

    @Id
    private String id;
    private PlayerPlaying player;
    private Croupier croupier;
    @JsonIgnore
    private List<Card> deck;
    private GameStatus status;
    private boolean playerWins;

    {
        this.croupier = new Croupier();
        this.deck = new ArrayList<>();
        this.status = GameStatus.IN_PROGRESS;
    }

}
