package de.uniks.pmws2223.uno.service;

import static de.uniks.pmws2223.uno.Constants.NOT_VALID;

public class GameServiceException extends Throwable {
    /**
     * This exception is used if there is an incorrect LOGIC behind this game service.
     * @param message the additional message
     */
    public GameServiceException( String message ) {
        super(NOT_VALID + ": "+ message);
    }
}
