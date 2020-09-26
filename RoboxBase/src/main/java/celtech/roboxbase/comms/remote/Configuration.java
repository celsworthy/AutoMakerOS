package celtech.roboxbase.comms.remote;

/**
 *
 * @author Ian
 */
public class Configuration
{
    public static final int remotePort = 8080;
    public static final String discoveryService = "/discovery";
    
    /**
     * Admin API
     */
  public static final String adminAPIService = "/admin";
    public static final String shutdown = "/shutdown";

    /**
     * Low Level API
     */
    public static final String lowLevelAPIService = "/printerControl";
    public static final String publicAPIService = "/remoteControl";
    public static final String connectService = "/connect";
    public static final String disconnectService = "/disconnect";
    public static final String writeDataService = "/writeData";
    public static final String sendStatisticsService = "/sendStatistics";
    public static final String retrieveStatisticsService = "/retrieveStatistics";
    public static final String sendCameraDataService = "/sendCameraData";
    public static final String retrieveCameraDataService = "/retrieveCameraData";
    public static final String overrideFilamentService = "/overrideFilament";
    public static final String clearAllErrorsService = "/clearAllErrors";
    public static final String clearErrorService = "/clearError";

    /**
     * Camera API
     */
    public static final String cameraAPIService = "/cameraControl";
}
