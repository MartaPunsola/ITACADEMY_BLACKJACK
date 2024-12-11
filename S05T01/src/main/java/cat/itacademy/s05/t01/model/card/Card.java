package cat.itacademy.s05.t01.model.card;

import cat.itacademy.s05.t01.model.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Card {

    private int value;
    private Suit suit;

    public Card(Suit suit) {
        this.suit = suit;
    }
}
