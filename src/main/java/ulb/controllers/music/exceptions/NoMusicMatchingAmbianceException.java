package ulb.controllers.music.exceptions;

public class NoMusicMatchingAmbianceException extends RuntimeException {
    public NoMusicMatchingAmbianceException(String message) {
        super(message);
    }
}
