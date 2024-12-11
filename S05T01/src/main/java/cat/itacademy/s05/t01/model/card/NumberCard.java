package cat.itacademy.s05.t01.model.card;

import cat.itacademy.s05.t01.model.enums.Suit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberCard extends Card {

    public NumberCard(int value, Suit suit) {
        super(value, suit);
    }
}
