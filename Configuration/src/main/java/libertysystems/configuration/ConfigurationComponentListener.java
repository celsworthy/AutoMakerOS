/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.configuration;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ConfigurationComponentListener implements ConfigurationListener
{

    private String componentName = null;

    public ConfigurationComponentListener(String componentName)
    {
        this.componentName = componentName;
    }

    @Override
    public void configurationChanged(ConfigurationEvent ce)
    {
        //Only triggers AFTER the value has been changed for the moment...
        if (ce.isBeforeUpdate() == false)
        {
            
        }
    }
}
