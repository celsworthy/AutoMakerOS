package celtech.roboxbase.printerControl.model;

/**
 *
 * @author George Salter
 */
public enum PrinterConnection {
    /**
     * Local machine connected directly
     */
    LOCAL,
    /**
     * Remote machine connected through root
     */
    REMOTE,
    /**
     * Dummy printer for debugging purposes
     */
    DUMMY,
    /**
     * Offline printer for saving projects to file for any hardware
     */
    OFFLINE
}
