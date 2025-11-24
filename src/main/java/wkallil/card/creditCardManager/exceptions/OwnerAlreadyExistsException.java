package wkallil.card.creditCardManager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OwnerAlreadyExistsException extends RuntimeException {
    public OwnerAlreadyExistsException(String message) {
        super(message);
    }

    public OwnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }



}
