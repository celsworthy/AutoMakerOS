package celtech.roboxbase.events;

/**
 *
 * @author ianhudson
 */
public enum Event
{
    PRINTER_CONNECTED(false),
    PRINTER_DISCONNECTED(false),
    PRINT_JOB_STARTED(true),
    PRINT_JOB_COMPLETE(true);
    
    private final boolean includeInAudit;

    private Event(boolean includeInAudit)
    {
        this.includeInAudit = includeInAudit;
    }

    public boolean isIncludeInAudit()
    {
        return includeInAudit;
    }    
}
