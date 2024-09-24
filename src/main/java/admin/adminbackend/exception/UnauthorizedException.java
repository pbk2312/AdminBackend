package admin.adminbackend.exception;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(String message) {
        super(message);
    }
}
