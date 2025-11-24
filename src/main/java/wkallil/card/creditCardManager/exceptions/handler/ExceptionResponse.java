package wkallil.card.creditCardManager.exceptions.handler;

import java.sql.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {
}
