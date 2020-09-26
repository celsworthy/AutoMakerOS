/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.configuration.fileRepresentation.StylusSettings;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import java.util.Optional;
import javafx.collections.ObservableList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class StylusSettingsContainerTest extends BaseEnvironmentConfiguredTest
{
    @Test
    public void testLoadStylusSettings()
    {
        ObservableList<StylusSettings> stylusSettings = StylusSettingsContainer.getInstance()
                                                                               .getCompleteSettingsList();
        assertEquals(2, stylusSettings.size());
        
        Optional<StylusSettings> ssOpt = StylusSettingsContainer.getInstance()
                                                                .getSettingsByName("NotPresent");
        assertTrue(ssOpt.isEmpty());
        ssOpt = StylusSettingsContainer.getInstance()
                                       .getSettingsByName("Biro");
        assertTrue(ssOpt.isPresent());
    }    
}
