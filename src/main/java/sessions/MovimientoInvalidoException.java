package sessions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class MovimientoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public MovimientoInvalidoException(String message) {
        super(message);
    }

}
