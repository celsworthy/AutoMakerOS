package celtech.automaker;

/**
 *
 * @author Ian
 */
public enum InterAppParameterType
{

    PROJECT_NAME("projectName"),
    MODEL_NAME("modelName"),
    DONT_GROUP_MODELS("dontGroupOnLoad");

    private final String parameterAsText;

    private InterAppParameterType(String parameterAsText)
    {
        this.parameterAsText = parameterAsText;
    }

    public String getParameterAsText()
    {
        return parameterAsText;
    }

    public static InterAppParameterType fromTextValue(String textValue)
    {
        InterAppParameterType foundType = null;
        
        for (InterAppParameterType typeToExamine : InterAppParameterType.values())
        {
            if (typeToExamine.getParameterAsText().equals(textValue))
            {
                foundType = typeToExamine;
                break;
            }
        }
        
        return foundType;
    }
}
