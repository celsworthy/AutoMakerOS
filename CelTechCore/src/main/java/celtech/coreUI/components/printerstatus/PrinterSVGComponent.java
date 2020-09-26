/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.printerstatus;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.Node;

/**
 *
 * @author tony
 */
public class PrinterSVGComponent extends Pane
{
    private final String printerIconSuffix = "PrinterIcon";
    private final String defaultPrinterTypeCode = "RBX01";
    Pane printerIcon = null;

    @FXML
    Pane readyIcon;
    @FXML
    Pane printingIcon;
    @FXML
    Pane pausedIcon;
    @FXML
    Pane notificationIcon;
    @FXML
    Pane errorIcon;
    @FXML
    Pane rootIndicator;

    private void hideAllIcons()
    {
        readyIcon.setVisible(false);
        printingIcon.setVisible(false);
        pausedIcon.setVisible(false);
        notificationIcon.setVisible(false);
    }

    public PrinterSVGComponent()
    {
        URL fxml = getClass().getResource("/celtech/resources/fxml/printerstatus/printerSVG.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
        
        errorIcon.setVisible(false);
    }

    public void setPrinterIcon(String printerTypeCode)
    {
        if (printerIcon != null)
        {
            printerIcon.setVisible(false);
            printerIcon = null;
        }
        
        Pane defaultPrinterIcon = null;
        String printerIconName = printerTypeCode.toUpperCase() + printerIconSuffix;
        String defaultPrinterIconName = defaultPrinterTypeCode + printerIconSuffix;
        for (Node pNode : getChildren())
        {
            String pNodeName = pNode.getId();
            if (pNodeName != null && pNodeName.endsWith(printerIconSuffix))
            {
                Pane p =  (Pane)pNode;
                if (pNodeName.equals(printerIconName))
                    printerIcon = p;
                else
                {
                    p.setVisible(false);
                    if (defaultPrinterIcon == null && pNodeName.equals(defaultPrinterIconName))
                        defaultPrinterIcon = p;
                }
                
            }
        }
        if (printerIcon == null)
            printerIcon = defaultPrinterIcon;
        
        if (printerIcon != null)
            printerIcon.setVisible(true);
    }
    
    public void setStatus(PrinterComponent.Status status)
    {
        hideAllIcons();

        switch (status)
        {
            case READY:
                readyIcon.setVisible(true);
                break;
            case PAUSED:
                pausedIcon.setVisible(true);
                break;
            case NOTIFICATION:
                notificationIcon.setVisible(true);
                break;
            case PRINTING:
                printingIcon.setVisible(true);
                break;
            case NO_INDICATOR:
                break;
        }
    }

    public void showErrorIndicator(boolean showErrorIndicator)
    {
        errorIcon.setVisible(showErrorIndicator);
    }

    public void setSize(double size)
    {
        Scale scale = new Scale(size / 260.0, size / 260.0, 0, 0);
        getTransforms().clear();
        getTransforms().add(scale);
    }

    public void setIsRoot(boolean isARoot)
    {
        rootIndicator.setVisible(isARoot);
    }
}
