package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostResponseException extends RuntimeException {

    public PostResponseException(String message) {
        super(message);
    }
}
