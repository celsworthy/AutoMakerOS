/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public enum RoboxCommsState
{

    /**
     *
     */
    FOUND,
    /**
     *
     */
    CHECKING_FIRMWARE,
    /**
     *
     */
    CHECKING_ID,
    /**
     *
     */
    RESETTING_ID,
    /**
     *
     */
    DETERMINING_PRINTER_STATUS,
    /**
     *
     */
    CONNECTED,
    
    DISCONNECTED,
    
    SHUTTING_DOWN

}
