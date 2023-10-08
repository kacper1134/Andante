package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TopicNotFoundException extends TopicException {

    public TopicNotFoundException(String message) {
        super(message);
    }
}
