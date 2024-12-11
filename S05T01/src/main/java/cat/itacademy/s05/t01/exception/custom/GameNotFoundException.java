package cat.itacademy.s05.t01.exception.custom;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(String message) {
        super(message);
    }
}
