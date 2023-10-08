package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostNotFoundException extends PostException {

    public PostNotFoundException(String message) {
        super(message);
    }
}
