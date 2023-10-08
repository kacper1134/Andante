package org.andante.activity.controller.decoder;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JWTTokenDecoder {
    private final JwtDecoder decoder;
    public Jwt decode(String authorizationHeader) {
        String[] authorizationHeaderParts = authorizationHeader.split(" ");
        String token = authorizationHeaderParts[1];
        return decoder.decode(token);
    }
}
