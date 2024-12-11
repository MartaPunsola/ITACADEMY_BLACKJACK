package cat.itacademy.s05.t01.model.card;

import cat.itacademy.s05.t01.model.enums.Figure;
import cat.itacademy.s05.t01.model.enums.Suit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtCard extends Card {

    private Figure figure;

    public CourtCard(int value, Suit suit, Figure figure) {
        super(value, suit);
        this.figure = figure;
    }
}
