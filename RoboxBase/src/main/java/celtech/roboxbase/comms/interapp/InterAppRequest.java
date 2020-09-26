package celtech.roboxbase.comms.interapp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author ianhudson
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class InterAppRequest
{
   
}
