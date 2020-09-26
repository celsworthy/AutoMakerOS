/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.roboxbase.postprocessor;


/**
 *
 * @author Ian
 */
public class RoboxiserResult
{
    private boolean success = false;
    private PrintJobStatistics printJobStatistics;
    
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
     * @return the roboxisedStatistics
     */
    public PrintJobStatistics getPrintJobStatistics()
    {
        return printJobStatistics;
    }

    /**
     * @param roboxisedStatistics the roboxisedStatistics to set
     */
    public void setRoboxisedStatistics(
        PrintJobStatistics roboxisedStatistics)
    {
        this.printJobStatistics = roboxisedStatistics;
    }

   
    
    
}
