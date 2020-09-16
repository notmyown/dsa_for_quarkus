package de.nmo.dsa.roller.error;

public class InvalidLoginException extends GenericException {
    public InvalidLoginException(String error_creating_meeting) {
        super(error_creating_meeting, null);
    }
}
