package cat.itacademy.s05.t01.model.player;

import cat.itacademy.s05.t01.model.card.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Participant {

    private List<Card> hand;
    private int handValue;

    {
        this.hand = new ArrayList<>();
    }
}
