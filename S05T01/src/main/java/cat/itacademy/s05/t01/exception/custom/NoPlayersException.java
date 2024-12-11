package cat.itacademy.s05.t01.exception.custom;

public class NoPlayersException extends RuntimeException {

    public NoPlayersException(String message) {
        super(message);
    }
}
