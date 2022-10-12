package jangseop.myleague.exception;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long id) {
        super("Could not find Team " + id);
    }
}
