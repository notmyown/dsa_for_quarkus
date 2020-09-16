package de.nmo.dsa.roller.error;

public class InvalidUserException extends GenericException {
    public InvalidUserException(String no_user_with_id) {
        super(no_user_with_id, null);
    }
}
