/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase;

/**
 *
 * @author tony
 */
public enum SystemErrorHandlerOptions
{
    
    ABORT(1),
    CLEAR_CONTINUE(2),
    RETRY(4),
    OK(8),
    OK_ABORT(16),
    OK_CONTINUE(32);
    
    private final int flag;

    private SystemErrorHandlerOptions(int flag)
    {
        this.flag = flag;
    }

    public String getErrorTitleKey()
    {
        return "error.handler." + name() + ".title";
    }

    public String getErrorMessageKey()
    {
        return "error.handler." + name() + ".message";
    }

    public int getFlag()
    {
        return flag;
    }
}
