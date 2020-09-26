/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.coreUI.controllers.popups;

/**
 *
 * @author Ian
 */
public interface PopupCommandReceiver
{

    /**
     *
     * @param source
     */
    public void triggerSaveAs(Object source);     

    /**
     *
     * @param profileName 
     */
    public void triggerSave(Object profile);     
}
