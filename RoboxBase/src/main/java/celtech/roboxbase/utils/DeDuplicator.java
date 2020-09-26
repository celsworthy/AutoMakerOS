/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.utils;

import java.util.Collection;

/**
 *
 * @author tony
 */
public class DeDuplicator
{

    /**
     * If the given name is not already in the collection of currentNames then return the given name
     * unchanged. If the name exists in currentNames then return a changed name (based on the
     * original name) that does not exist in currentNames.
     *
     * @param name
     * @param currentNames
     * @return
     */
    public static String suggestNonDuplicateNameCopy(String name,
        Collection<String> currentNames)
    {
        int attempt = 1;
        String suggestedName = name;
        while (currentNames.contains(suggestedName))
        {
            switch (attempt)
            {
                case 1:
                    suggestedName = name + " (Copy)";
                    break;
                case 2:
                    suggestedName = name + " (Another Copy)";
                    break;
                default:
                    suggestedName = name + " (Copy #" + attempt + ")";
            }

            attempt++;
        }
        return suggestedName;
    }

    public static String suggestNonDuplicateName(String name,
        Collection<String> currentNames)
    {
        int attempt = 1;
        String suggestedName = name;
        while (currentNames.contains(suggestedName))
        {
            suggestedName = name + " (" + attempt + ")";
            attempt++;
        }
        return suggestedName;
    }

}
