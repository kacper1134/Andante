package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserConflictException extends UserException {

    public UserConflictException(String message) {
        super(message);
    }
}
