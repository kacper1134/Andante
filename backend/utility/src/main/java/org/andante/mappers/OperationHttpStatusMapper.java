package org.andante.mappers;

import org.andante.enums.OperationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class OperationHttpStatusMapper {

    public HttpStatus toHttpStatus(OperationStatus operationStatus) {
        switch (operationStatus) {
            case OK:
                return HttpStatus.OK;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CLIENT_ERROR:
                return HttpStatus.PRECONDITION_FAILED;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
