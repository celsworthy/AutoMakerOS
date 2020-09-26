/*
 * Copyright 2014 CEL UK
 */
package celtech.utils;

import celtech.configuration.ApplicationConfiguration;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

/**
 *
 * @author tony
 */
public class StringMetrics
{

    public static double getWidthOfString(String str, String styleClass, int fontSize)
    {
        final Text text = new Text(str);
        Scene scene = new Scene(new Group(text));
        scene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
        text.getStyleClass().add(styleClass);
        text.setStyle("-fx-font-size:" + fontSize + "pt;");
        text.applyCss();

        return text.getLayoutBounds().getWidth();
    }

}
