package cat.itacademy.s05.t01.model.player;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "player")
public class Player {

    @Id
    private Long id;

    @Column("username")
    private String username;
    @Column("balance")
    private int balance;
    @Column("games_played")
    private int gamesPlayed;
    @Column("games_won")
    private int gamesWon;

    {
        this.balance = 150;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
    }

    public Player(String username) {
        this.username = username;
    }
}
