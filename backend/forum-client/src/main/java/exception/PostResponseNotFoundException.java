package exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PostResponseNotFoundException extends PostResponseException {

    public PostResponseNotFoundException(String message) {
        super(message);
    }
}
