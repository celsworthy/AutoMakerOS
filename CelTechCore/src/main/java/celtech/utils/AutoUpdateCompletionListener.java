/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.utils;

/**
 *
 * @author Ian
 */
public interface AutoUpdateCompletionListener
{

    /**
     *
     * @param requiresShutdown
     */
    public void autoUpdateComplete(boolean requiresShutdown);
}
