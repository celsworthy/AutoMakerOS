package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;

/**
 *
 * @author Ian
 */
public class LibraryMenuPanelController extends MenuPanelController
{

    private InnerPanelDetails cameraProfileDetails = null;
    private CameraProfilesPanelController cameraProfileDetailsController = null;


    public LibraryMenuPanelController()
    {
        paneli18Name = "libraryMenu.title";
    }

    @Override
    protected void setupInnerPanels()
    {
        loadInnerPanel(
                ApplicationConfiguration.fxmlPanelResourcePath + "filamentLibraryPanel.fxml",
                new FilamentLibraryPanelController());

        profileDetailsController = new ProfileLibraryPanelController();
        profileDetails = loadInnerPanel(
                ApplicationConfiguration.fxmlUtilityPanelResourcePath + "profileDetails.fxml",
                profileDetailsController);
        
        cameraProfileDetailsController = new CameraProfilesPanelController();
        cameraProfileDetails = loadInnerPanel(
                ApplicationConfiguration.fxmlPanelResourcePath + "cameraProfilesPanel.fxml",
                cameraProfileDetailsController);
    }
    
    public void showAndSelectPrintProfile(RoboxProfile roboxProfile)
    {
        String profileMenuItemName = Lookup.i18n(profileDetails.innerPanel.getMenuTitle());
        panelMenu.selectItemOfName(profileMenuItemName);
        profileDetailsController.setAndSelectPrintProfile(roboxProfile);
    }

    public void showAndSelectCameraProfile(CameraProfile profile)
    {
        String cameraProfileMenuItemName = Lookup.i18n(cameraProfileDetails.innerPanel.getMenuTitle());
        panelMenu.selectItemOfName(cameraProfileMenuItemName);
        cameraProfileDetailsController.setAndSelectCameraProfile(profile);
    }
}
