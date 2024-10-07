package admin.adminbackend.exception;

public class IncorrectPasswordException extends RuntimeException{

    public IncorrectPasswordException() {
        super("비밀번호가 잘못되었습니다.");
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }

    public IncorrectPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectPasswordException(Throwable cause) {
        super(cause);
    }
}
