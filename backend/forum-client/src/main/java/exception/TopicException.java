package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TopicException extends RuntimeException {

    public TopicException(String message) {
        super(message);
    }
}
