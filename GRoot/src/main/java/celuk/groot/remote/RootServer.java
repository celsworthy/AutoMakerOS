package celuk.groot.remote;

import celuk.groot.GRootPIN;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class RootServer extends Updater {
    
    public static final int CONNECT_TIMEOUT_LONG = 2000;
    public static final int CONNECT_TIMEOUT_SHORT = 300;
    public static final int READ_TIMEOUT_LONG = 15000;
    public static final int READ_TIMEOUT_SHORT = 1500;
    protected static final String USER_AGENT = "CEL Robox";
    protected static final String HTTP_PREFIX = "http://";
    //protected static final String LOCAL_HOST = "localhost";
    //protected static final String HTTP_ADDRESS = "192.168.1.141";
    //protected static final String HTTP_ADDRESS = "localhost";
    //protected static final String HTTP_PORT = "8080";
    private static final String UPDATE_PIN_COMMAND = "/api/admin/updatePIN";
    private static final String RESET_PIN_COMMAND = "/api/admin/resetPIN";
    
    private static final String ENABLE_DISABLE_WIFI_COMMAND = "/api/admin/enableDisableWifi";
    private static final String GET_CURRENT_WIFI_STATE_COMMAND = "/api/admin/getCurrentWifiState";
    private static final String SET_SERVER_NAME_COMMAND = "/api/admin/setServerName";
    private static final String SET_WIFI_CREDENTIALS_COMMAND = "/api/admin/setWiFiCredentials";
    private static final String LIST_PRINTERS_COMMAND = "/api/discovery/listPrinters";
    private static final String WHO_ARE_YOU_COMMAND = "/api/discovery/whoareyou?pc=no&rid=yes&ru=yes";
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ExecutorService executorService;
    protected SimpleStringProperty pinProperty = new SimpleStringProperty("");

    private final SimpleBooleanProperty currentPrinterMapHeartbeatProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty authorisedProperty = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<ServerStatusResponse> currentStatusProperty = new SimpleObjectProperty<>(null);
    private final SimpleBooleanProperty currentStatusHeartbeatProperty = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<WifiStatusResponse> wifiStatusProperty = new SimpleObjectProperty<>(null);
    private final ObservableMap<String, RootPrinter> currentPrinterMap = FXCollections.observableMap(new HashMap<>());
    
    private final String hostAddress;
    private final String hostPort;
    private final String configurationDirectory;
    
    public RootServer(String hostAddress, String hostPort, String configurationDirectory) {
        super();
        this.hostAddress = hostAddress;
        this.hostPort = hostPort;
        this.configurationDirectory = configurationDirectory;
        ThreadFactory threadFactory = (Runnable runnable) -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        };
        executorService = Executors.newFixedThreadPool(6, threadFactory);
        
    }
    
    protected ExecutorService getExecutorService() {
        return executorService;
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    public SimpleBooleanProperty getCurrentPrinterMapHeartbeatProperty() {
        return currentPrinterMapHeartbeatProperty;
    }
    
    public ObservableMap<String, RootPrinter> getCurrentPrinterMap() {
        return currentPrinterMap;
    }
    
    public SimpleBooleanProperty getAuthorisedProperty() {
        return authorisedProperty;
    }

    public SimpleObjectProperty<ServerStatusResponse> getCurrentStatusProperty() {
        return currentStatusProperty;
    }
    
    public SimpleBooleanProperty getCurrentStatusHeartbeatProperty() {
        return currentStatusHeartbeatProperty;
    }
    
    public SimpleObjectProperty<WifiStatusResponse> getWifiStatusProperty() {
        return wifiStatusProperty;
    }

    public String getName() {
        return currentStatusProperty.get().getName();
    }
    
    public String getServerIP() {
        return currentStatusProperty.get().getServerIP();
    }

    public String getServerVersion() {
        return currentStatusProperty.get().getServerVersion();
    }

    public List<String> getPrinterColours() {
        return currentStatusProperty.get().getPrinterColours();
    }
    
    public SimpleStringProperty getPINProperty() {
        return pinProperty;
    }
    
    public String getPIN() {
        return pinProperty.get();
    }
    
    public void initPIN(String pin) {
        if (pin.isBlank()) {
            GRootPIN persistentPIN = GRootPIN.loadFromJSON(configurationDirectory);
            pinProperty.set(persistentPIN.getPIN());
        }
        else
            setPIN(pin);
    }

    public void setPIN(String pin) {
        pinProperty.set(pin);
        if (!configurationDirectory.isBlank()) {
            GRootPIN persistentPIN = new GRootPIN();
            persistentPIN.setPIN(pin);
            persistentPIN.saveToJSON(configurationDirectory);
        }
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getHostPort() {
        return hostPort;
    }

    protected byte[] makeHTTPRequest(String request, boolean isGetRequest, String content, int connectTimeout, int readTimeout) {
        String url = HTTP_PREFIX + hostAddress + ":" + hostPort + request;
        byte[] requestData = null;

        long t1 = System.currentTimeMillis();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod(isGetRequest ? "GET" : "POST");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Authorization", "Basic " + StringToBase64Encoder.encode("root:" + getPIN()));

            con.setConnectTimeout(connectTimeout);
            con.setReadTimeout(readTimeout);

            if (content != null) {
                byte[] data = content.getBytes(StandardCharsets.UTF_8);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Content-Length", "" + data.length);
                con.getOutputStream().write(data);
            }

            int responseCode = con.getResponseCode();

            switch (responseCode) {
                case 200:
                    //System.out.println("200 response for request \"" + request + "\"");
                    int availChars = con.getInputStream().available();
                    requestData = new byte[availChars];
                    con.getInputStream().read(requestData, 0, availChars);
                    authorisedProperty.set(true);
                    break;
                case 204:
                    //System.out.println("204 responsefor request \"" + request + "\"");
                    requestData = new byte[0];
                    authorisedProperty.set(true);
                    break;
                case 401:
                    //System.out.println("401 response - unauthorised request \"" + request + "\"");
                    authorisedProperty.set(false);
                    break;
                default:
                    System.err.println("Invalid response (" + Integer.toString(responseCode) +") after making request \"" + request + "\" from @" + hostAddress + ":" + hostPort);
                    break;
            }
        } 
        catch (java.net.SocketTimeoutException ex) {
            long t2 = System.currentTimeMillis();
            //System.err.println("Timeout whilst making request \"" + request + "\" from @" + hostAddress + ":" + hostPort + " - time taken = " + Long.toString(t2 - t1));
        }
        catch (IOException ex) {
            //System.err.println("Error whilst making request \"" + request + "\" from @" + hostAddress + ":" + hostPort + " - " + ex);
        }
        
        return requestData;
    }

    public <R> Future<R> runBackgroundTask( Callable<R> backgroundTask) {
        return executorService.submit(backgroundTask);
    }

    public <R> Future<R> runRequestTask(String command, boolean isGetRequest, int readTimeout, String content, BiFunction<byte[], ObjectMapper, R> responseMapper, Consumer<Exception> exceptionReporter) {
        return executorService.submit(() -> {
            R response = null;
            try {
                byte[] requestData = makeHTTPRequest(command, isGetRequest, content, CONNECT_TIMEOUT_SHORT, readTimeout);
                if (requestData != null) {
                    //System.out.println("Calling response mapper for \"" + command + "\"");
                    response = responseMapper.apply(requestData, mapper);
                }
                else if (exceptionReporter != null) {
                    exceptionReporter.accept(null);
                }
                    
            }
            catch (Exception ex) {
                System.err.println("Error whilst requesting \"" + command + "\" from @" + hostAddress + ":" + hostPort + " - " + ex);
                if (exceptionReporter != null) {
                    exceptionReporter.accept(ex);
                }
            }
            return response;
        });
    }

    public Future<ObservableMap<String, RootPrinter>> runListAttachedPrintersTask() {
        return runRequestTask(LIST_PRINTERS_COMMAND, true, READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating server printer list");
                        ListPrintersResponse listPrintersResponse = jMapper.readValue(requestData, ListPrintersResponse.class);
                        listPrintersResponse.getPrinterIDs()
                                .stream()
                                    .filter(pid -> !currentPrinterMap.containsKey(pid))
                                    .forEach( pid -> {
                                        RootPrinter p = currentPrinterMap.get(pid);
                                        if (p == null) {
                                            //System.out.println("Creating new printer for \"" + pid + "\"");
                                            p = new RootPrinter(this, pid);
                                            p.startUpdating(updateInterval);
                                            currentPrinterMap.put(pid, p);
                                        }
                                    });
                        List<Entry<String, RootPrinter>> lostPrinters = currentPrinterMap.entrySet()
                                .stream()
                                .filter(e -> !listPrintersResponse.getPrinterIDs().contains(e.getKey()))
                                .collect(Collectors.toList());
                        lostPrinters.forEach(e -> {
                            e.getValue().stopUpdating();
                            currentPrinterMap.remove(e.getKey());
                        });
                        // The original idea was to have a listener on the currentPrinterMapHeartbeatProperty, which was to be called every
                        // time the map is updated. Unfortunately, this fails to trigger for the initial updates.
                        // So instead we have a heartbeat property that is toggled every time the map is updated.
                        currentPrinterMapHeartbeatProperty.set(!currentPrinterMapHeartbeatProperty.get());
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding printer list from @" + hostAddress + ":" + hostPort + " - " + ex);
                }
                return currentPrinterMap;
            },
            null);
    }

    public Future<ServerStatusResponse> runRequestServerStatusTask() {
        return runRequestTask(WHO_ARE_YOU_COMMAND, true, READ_TIMEOUT_SHORT, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                ServerStatusResponse serverStatus = null;
                try {
                    if (requestData.length > 0) {
                        //String s = new String(requestData);
                        //System.out.println("Updating server status - \"" + s + "\"");
                        serverStatus = mapper.readValue(requestData, ServerStatusResponse.class);
                        currentStatusProperty.set(serverStatus);
                        currentStatusHeartbeatProperty.set(!currentStatusHeartbeatProperty.get());
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding server status from @" + hostAddress + ":" + hostPort + " - " + ex);
                }
                return serverStatus;
            },
            (Exception ex) -> {
                currentStatusProperty.set(null);
                currentStatusHeartbeatProperty.set(!currentStatusHeartbeatProperty.get());
            });
    }
    
    public Future<Void> runUpdatePINTask(String pin) {
        String data = String.format("\"%s\"", pin);
        return runRequestTask(UPDATE_PIN_COMMAND, false, READ_TIMEOUT_SHORT, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return null;
            },
            null);
    }

    public Future<Void> runResetPINTask(String serial) {
        String data = String.format("\"%s\"", serial);
        return runRequestTask(RESET_PIN_COMMAND, false, READ_TIMEOUT_SHORT, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return null;
            },
            null);
    }
    
    public Future<WifiStatusResponse> runRequestWifiStatusTask() {
        return runRequestTask(GET_CURRENT_WIFI_STATE_COMMAND, false, READ_TIMEOUT_LONG, null,
            (byte[] requestData, ObjectMapper jMapper) -> {
                WifiStatusResponse wifiStatus = null;
                try {
                    if (requestData.length > 0) {
                        //System.out.println("Updating wifi status");
                        wifiStatus = mapper.readValue(requestData, WifiStatusResponse.class);
                        wifiStatusProperty.set(wifiStatus);
                    }
                }
                catch (IOException ex) {
                    System.err.println("Error whilst decoding printer list from @" + hostAddress + ":" + hostPort + " - " + ex);
                }
                return wifiStatus;
            },
            null);
    }
    
    public Future<Void> runSetServerNameTask(String serverName) {
        String data = String.format("\"%s\"", serverName);
        return runRequestTask(SET_SERVER_NAME_COMMAND, false, READ_TIMEOUT_SHORT, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return null;
            },
            null);
    }
    
    public Future<Void> runEnableDisableWifiTask(boolean enableWifi) {
        String data = enableWifi ? "\"true\""
                                 : "\"false\"";
        return runRequestTask(ENABLE_DISABLE_WIFI_COMMAND, false, READ_TIMEOUT_LONG, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return null;
            },
            null);
    }
    
    public Future<Void> runSetWiFiCredentialsTask(String ssid, String password) {
        String data = String.format("\"%s:%s\"", ssid, password);
        return runRequestTask(SET_WIFI_CREDENTIALS_COMMAND, false, READ_TIMEOUT_LONG, data,
            (byte[] requestData, ObjectMapper jMapper) -> {
                return null;
            },
            null);
    }

    @Override
    protected void update() {
        runRequestServerStatusTask();
        runListAttachedPrintersTask();
    }

    public boolean checkPrinterExists(String printerId) {
        return currentPrinterMap.containsKey(printerId);
    }
}
