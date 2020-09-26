/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

/**
 *
 * @author tony
 */
public class FXMLUtilities
{

    /**
     * Recursively add colons to all descendent labels that have the styleclass "colon".
     *
     * @param parentNode the node from which to start the recursion.
     */
    public static void addColonsToLabels(Node parentNode)
    {
        if (parentNode instanceof Label)
        {
            Label label = (Label) parentNode;
            addColonToLabel(label);
        } else if (parentNode instanceof Text)
        {
            addColonToText((Text) parentNode);
        } else if (parentNode instanceof Parent)
        {
            for (Node node : ((Parent) parentNode).getChildrenUnmodifiable())
            {
                if (node instanceof Parent || node instanceof Text)
                {
                    addColonsToLabels(node);
                }

                if (node instanceof TabPane)
                {
                    TabPane tabPane = (TabPane) node;
                    for (Tab tab : tabPane.getTabs())
                    {
                        Node content = tab.getContent();
                        if (content instanceof Parent)
                        {
                            addColonsToLabels((Parent) content);
                        }
                    }
                }
                
                if (node instanceof ScrollPane)
                {
                    ScrollPane scrollPane = (ScrollPane) node;
                    Node content = scrollPane.getContent();
                    if (content instanceof Parent)
                    {
                        addColonsToLabels((Parent) content);
                    }
                }
            }
        }
    }

    private static void addColonToLabel(Label label)
    {
        if (label.getStyleClass().contains("colon") && !label.getText().endsWith(":"))
        {
            label.setText(label.getText() + ":");
        }
    }

    private static void addColonToText(Text text)
    {
        if (text.getStyleClass().contains("colon"))
        {
            text.setText(text.getText() + ":");
        }
    }
}
