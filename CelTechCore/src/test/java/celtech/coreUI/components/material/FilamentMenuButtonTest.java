package celtech.coreUI.components.material;

import celtech.FXTest;
import celtech.JavaFXConfiguredTest;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Ian
 */
@Category(FXTest.class)
public class FilamentMenuButtonTest extends JavaFXConfiguredTest
{

    @Test
    public void testCategoryComparator()
    {
        Filament roboxCategory = FilamentContainer.UNKNOWN_FILAMENT.clone();
        roboxCategory.setCategory("Robox");
        Filament aCategory = FilamentContainer.UNKNOWN_FILAMENT.clone();
        aCategory.setCategory("A Category");
        Filament zCategory = FilamentContainer.UNKNOWN_FILAMENT.clone();
        zCategory.setCategory("Z Category");
        Filament customCategory = FilamentContainer.UNKNOWN_FILAMENT.clone();
        customCategory.setCategory("Custom");

        //Basic alpha sort check
        assertTrue(FilamentMenuButton.byCategory.compare(aCategory, zCategory) < 0);
        assertTrue(FilamentMenuButton.byCategory.compare(aCategory, aCategory) == 0);
        assertTrue(FilamentMenuButton.byCategory.compare(zCategory, aCategory) > 0);
        //Check Robox always comes first
        assertTrue(FilamentMenuButton.byCategory.compare(roboxCategory, zCategory) < 0);
        assertTrue(FilamentMenuButton.byCategory.compare(roboxCategory, aCategory) < 0);
        assertTrue(FilamentMenuButton.byCategory.compare(zCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byCategory.compare(aCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byCategory.compare(customCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byCategory.compare(roboxCategory, roboxCategory) == 0);
        //Check Custom always comes last
        assertTrue(FilamentMenuButton.byCategory.compare(customCategory, zCategory) > 0);
        assertTrue(FilamentMenuButton.byCategory.compare(customCategory, aCategory) > 0);
        assertTrue(FilamentMenuButton.byCategory.compare(zCategory, customCategory) < 0);
        assertTrue(FilamentMenuButton.byCategory.compare(aCategory, customCategory) < 0);
        assertTrue(FilamentMenuButton.byCategory.compare(customCategory, customCategory) == 0);
    }

    @Test
    public void testCategoryByNameComparator()
    {
        String roboxCategory = "Robox";
        String aCategory = "A Category";
        String zCategory = "Z Category";
        String customCategory = "Custom";

        //Basic alpha sort check
        assertTrue(FilamentMenuButton.byBrandName.compare(aCategory, zCategory) < 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(aCategory, aCategory) == 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(zCategory, aCategory) > 0);
        //Check Robox always comes first
        assertTrue(FilamentMenuButton.byBrandName.compare(roboxCategory, zCategory) < 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(roboxCategory, aCategory) < 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(zCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(aCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(customCategory, roboxCategory) > 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(roboxCategory, roboxCategory) == 0);
        //Check Custom always comes last
        assertTrue(FilamentMenuButton.byBrandName.compare(customCategory, zCategory) > 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(customCategory, aCategory) > 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(zCategory, customCategory) < 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(aCategory, customCategory) < 0);
        assertTrue(FilamentMenuButton.byBrandName.compare(customCategory, customCategory) == 0);
    }

}
