/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.services.printing;

/**
 *
 * @author Ian
 */
public class GCodePrintResult
{
    private boolean success = false;
    private String printJobID = null;

    /**
     *
     * @return
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     *
     * @param success
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     *
     * @return
     */
    public String getPrintJobID()
    {
        return printJobID;
    }

    /**
     *
     * @param printJobID
     */
    public void setPrintJobID(String printJobID)
    {
        this.printJobID = printJobID;
    }
}
