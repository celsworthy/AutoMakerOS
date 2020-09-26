package celtech;

import celtech.coreUI.components.HyperlinkedLabelTest;
import celtech.coreUI.components.material.FilamentMenuButtonTest;
import celtech.coreUI.controllers.panels.GCodePanelControllerTest;
import celtech.utils.settingsgeneration.ProfileDetailsGenerator;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 * @author root
 */
@RunWith(Categories.class)
@IncludeCategory(FXTest.class)
@SuiteClasses({HyperlinkedLabelTest.class, FilamentMenuButtonTest.class, 
    GCodePanelControllerTest.class, ProfileDetailsGenerator.class})
public class JavaFXTestSuite {
    
}
