
package celtech.coreUI.components.tips;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Ian
 */
public class ConditionalText
{
    private ObservableValue<Boolean> appearanceCondition;
    private String i18nText;

    public ConditionalText(String i18nText, ObservableValue<Boolean> appearanceCondition)
    {
        this.i18nText = i18nText;
        this.appearanceCondition = appearanceCondition;
    }

    public ObservableValue<Boolean> getAppearanceCondition()
    {
        return appearanceCondition;
    }

    public void setAppearanceCondition(BooleanBinding appearanceCondition)
    {
        this.appearanceCondition = appearanceCondition;
    }

    public String getI18nText()
    {
        return i18nText;
    }

    public void setI18nText(String i18nText)
    {
        this.i18nText = i18nText;
    }
}
