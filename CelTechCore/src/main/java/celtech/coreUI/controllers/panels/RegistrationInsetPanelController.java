/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.appManager.ApplicationStatus;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 *
 * @author tony
 */
public class RegistrationInsetPanelController implements Initializable
{
    @FXML
    void backwardPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().returnToLastMode();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
     

    }

}
