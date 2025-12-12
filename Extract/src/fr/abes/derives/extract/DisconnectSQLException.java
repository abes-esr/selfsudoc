package fr.abes.derives.extract;

public class DisconnectSQLException extends Exception {

    private static final long serialVersionUID = -2101134464839281087L;

    public DisconnectSQLException() {
    }

    public DisconnectSQLException(String arg0) {
        super(arg0);
    }

    public DisconnectSQLException(Throwable arg0) {
        super(arg0);
    }

    public DisconnectSQLException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    @Override
    public String getMessage() {
        return "You shoud try to reconnect....\n" + super.getMessage();
    }

}
