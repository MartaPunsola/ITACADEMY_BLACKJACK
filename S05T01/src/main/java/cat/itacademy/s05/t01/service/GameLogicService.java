package cat.itacademy.s05.t01.service;

import cat.itacademy.s05.t01.exception.custom.InvalidMoveException;
import cat.itacademy.s05.t01.model.Game;
import cat.itacademy.s05.t01.model.card.Ace;
import cat.itacademy.s05.t01.model.card.Card;
import cat.itacademy.s05.t01.model.card.CourtCard;
import cat.itacademy.s05.t01.model.card.NumberCard;
import cat.itacademy.s05.t01.model.enums.*;
import cat.itacademy.s05.t01.model.player.Croupier;
import cat.itacademy.s05.t01.model.player.Participant;
import cat.itacademy.s05.t01.model.player.PlayerPlaying;
import cat.itacademy.s05.t01.service.impl.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class GameLogicService {

    @Autowired
    private PlayerServiceImpl playerService;

    public Mono<Game> initiateGame() {
        return Mono.just(new Game())
                .map(game -> {
                    game.setDeck(newDeck());
                    return game;
                });
    }

    private List<Card> newDeck() {
        List<Card> cards = new ArrayList<>();
        Arrays.stream(Suit.values())
                .forEach(suit -> {
                    cards.add(new Ace(suit));
                    for (int i = 2; i <= 10; i++) {
                        cards.add(new NumberCard(i, suit));
                    }
                    Arrays.stream(Figure.values())
                            .forEach(figure -> cards.add(new CourtCard(10, suit, figure)));
                });
        Collections.shuffle(cards);
        return cards;
    }

    private Mono<Game> dealInitialCards(Game game) {
        List<Card> deck = game.getDeck();
        PlayerPlaying player = game.getPlayer();
        Croupier croupier = game.getCroupier();

        dealCard(deck, player);
        dealCard(deck, player);
        setAceValue(player.getHand(), calculateHandValue(player));
        player.setHandValue(calculateHandValue(player));
        player.setStatus(PlayerStatus.PLAYING);

        dealCard(deck, croupier);
        dealCard(deck, croupier);
        setAceValue(croupier.getHand(), calculateHandValue(croupier));
        croupier.setHandValue(calculateHandValue(croupier));

        return Mono.just(game);
    }

    private void dealCard(List<Card> deck, Participant participant) {
        Card card;
        card = retrieveCardFromDeck(deck);
        participant.getHand().add(card);
    }

    private Card retrieveCardFromDeck(List<Card> deck) {
        Random random = new Random();
        int randomIndex = random.nextInt(deck.size());
        return deck.remove(randomIndex);
    }

    private int calculateHandValue(Participant participant) {
        return participant.getHand().stream().mapToInt(Card::getValue).sum();
    }

    private void setAceValue(List<Card> cards, int handValue) {
        for (Card card : cards) {
            if (card instanceof Ace) {
                card.setValue(1);
                handValue += 1;
            }
        }

        for (Card card : cards) {
            if (card instanceof Ace && handValue + 10 <= 21) {
                card.setValue(11);
                handValue += 10;
            }
        }
    }

    public Mono<Game> play(Game game, Move move, int bet) {
        PlayerPlaying player = game.getPlayer();
        playerBets(game, bet);
        dealInitialCards(game);
        if(player.getHandValue() == 21) {
            blackJackResult(game);
        }
        playerMakesMove(game, move);
        return Mono.just(game);
    }

    private Mono<Game> playerBets(Game game, int initialBet) {
        PlayerPlaying player = game.getPlayer();
        if(initialBet < 5 || initialBet > 150) {
            return Mono.error(new InvalidMoveException("Minimum bet: 5\nMaximum bet: 150"));
        }
        player.setInitialBet(initialBet);
        return Mono.just(game);
    }

    private Mono<Game> blackJackResult(Game game) {
        game.getPlayer().setStatus(PlayerStatus.BLACKJACK);
        return Mono.just(game);
    }

    public Mono<Game> playerMakesMove(Game game, Move move) {
        if(game.getPlayer().getInitialBet() == 0) {
            return Mono.error(new InvalidMoveException("It is compulsory to bet before making a move."));
        }
        switch (move) {
            case HIT:
                playerHits(game);
                break;
            case STAND:
                playerStands(game);
                break;
        }
        return Mono.just(game);
    }

    public Mono<Game> croupierMakesMove(Game game) {
        Croupier croupier = game.getCroupier();
        List<Card> deck = game.getDeck();

        if(calculateHandValue(croupier) < 17) {
            this.dealCard(deck, croupier);
            setAceValue(croupier.getHand(), calculateHandValue(croupier));
            croupier.setHandValue(calculateHandValue(croupier));
        }
        return Mono.just(game);
    }

    private Mono<Game> playerHits(Game game) {
        PlayerPlaying player = game.getPlayer();
        List<Card> deck = game.getDeck();

        player.setStatus(PlayerStatus.HIT);
        dealCard(deck, player);
        setAceValue(player.getHand(), calculateHandValue(player));
        player.setHandValue(calculateHandValue(player));

        if(player.getHandValue() > 21) {
            player.setStatus(PlayerStatus.BUST);
        } else if(player.getHandValue() == 21) {
            player.setStatus(PlayerStatus.STAND);
        }
        return Mono.just(game);
    }

    private Mono<Game> playerStands(Game game) {
        game.getPlayer().setStatus(PlayerStatus.STAND);
        return Mono.just(game);
    }

    public Mono<Game> determineFinalStatus(Game game) {
        PlayerPlaying player = game.getPlayer();
        int playerHand = game.getPlayer().getHandValue();
        int croupierHand = game.getCroupier().getHandValue();

        Mono<Game> gameResult;

        if(player.getStatus() == PlayerStatus.BLACKJACK) {
            gameResult = playerWins(game);
        } else if((playerHand <= 21) && (playerHand > croupierHand)) {
            gameResult = playerWins(game);
        } else if(croupierHand > 21) {
            gameResult = playerWins(game);
        } else if(player.getStatus() == PlayerStatus.BUST) {
            gameResult = playerLoses(game);
        } else if((playerHand <= 21) && (playerHand <= croupierHand)) {
            gameResult = playerLoses(game);
        } else {
            gameResult = Mono.just(game);
        }

        return gameResult.map(updatedGame -> {
            updatedGame.setStatus(GameStatus.FINISHED);
            return updatedGame;
        });
    }

    private Mono<Game> playerWins(Game game) {
        PlayerPlaying player = game.getPlayer();
        player.setStatus(PlayerStatus.WIN);
        player.setGamesWon(player.getGamesWon() + 1);
        game.setPlayerWins(true);

        return playerService.addToBalance(player.getId(), player.getInitialBet())
                .then(playerService.addWonGame(player.getId()))
                .thenReturn(game);
    }

    private Mono<Game> playerLoses(Game game) {
        PlayerPlaying player = game.getPlayer();
        player.setStatus(PlayerStatus.LOSE);
        game.setPlayerWins(false);
        return playerService.subtractFromBalance(player.getId(), player.getInitialBet())
                .then(Mono.just(game));
    }

}
