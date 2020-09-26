package celtech.roboxbase;

import celtech.roboxbase.appManager.ConsoleSystemNotificationManager;
import celtech.roboxbase.appManager.SystemNotificationManager;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.datafileaccessors.SlicerMappingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.SlicerMappings;
import celtech.roboxbase.postprocessor.GCodeOutputWriter;
import celtech.roboxbase.postprocessor.GCodeOutputWriterFactory;
import celtech.roboxbase.postprocessor.LiveGCodeOutputWriter;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterListChangesNotifier;
import celtech.roboxbase.utils.tasks.LiveTaskExecutor;
import celtech.roboxbase.utils.tasks.TaskExecutor;
import celuk.language.I18n;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import libertysystems.stenographer.LogLevel;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class BaseLookup
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            BaseLookup.class.getName());

    private static TaskExecutor taskExecutor;
    private static SlicerMappings slicerMappings;
    private static GCodeOutputWriterFactory<GCodeOutputWriter> postProcessorGCodeOutputWriterFactory;
    private static SystemNotificationManager systemNotificationHandler;
    private static boolean shuttingDown = false;

    private static PrinterListChangesNotifier printerListChangesNotifier;
    private static final ObservableList<Printer> CONNECTED_PRINTERS = FXCollections.observableArrayList();
    private static final ObservableList<Printer> CONNECTED_PRINTERS_UNMODIFIABLE = FXCollections.unmodifiableObservableList(CONNECTED_PRINTERS);

    private static final ObservableList<CameraInfo> CONNECTED_CAMS = FXCollections.observableArrayList();
    private static final ObservableList<CameraInfo> CONNECTED_CAMS_UNMODIFIABLE = FXCollections.unmodifiableObservableList(CONNECTED_CAMS);
    
    public static final ObservableList<File> MOUNTED_USB_DIRECTORIES = FXCollections.observableArrayList();
    
    private static Set<Locale> availableLocales = null;

    /**
     * The database of known filaments.
     */
    private static FilamentContainer filamentContainer;

    public static ResourceBundle getLanguageBundle()
    {
        return I18n.getLanguageBundle();
    }

    public static String i18n(String stringId)
    {
        return I18n.t(stringId);
    }

    /**
     * Strings containing templates (eg *T14) should be substituted with the
     * correct text.
     *
     * @param langString
     * @return
     */
    public static String substituteTemplates(String langString)
    {
        return I18n.substituteTemplates(langString);
    }

    public static TaskExecutor getTaskExecutor()
    {
        return taskExecutor;
    }

    public static void setTaskExecutor(TaskExecutor taskExecutor)
    {
        BaseLookup.taskExecutor = taskExecutor;
    }

    public static void setSlicerMappings(SlicerMappings slicerMappings)
    {
        BaseLookup.slicerMappings = slicerMappings;
    }

    public static SlicerMappings getSlicerMappings()
    {
        return slicerMappings;
    }

    public static GCodeOutputWriterFactory getPostProcessorOutputWriterFactory()
    {
        return postProcessorGCodeOutputWriterFactory;
    }

    public static void setPostProcessorOutputWriterFactory(
            GCodeOutputWriterFactory<GCodeOutputWriter> factory)
    {
        postProcessorGCodeOutputWriterFactory = factory;
    }

    public static SystemNotificationManager getSystemNotificationHandler()
    {
        return systemNotificationHandler;
    }

    public static void setSystemNotificationHandler(
            SystemNotificationManager systemNotificationHandler)
    {
        BaseLookup.systemNotificationHandler = systemNotificationHandler;
    }

    public static boolean isShuttingDown()
    {
        return shuttingDown;
    }

    public static void setShuttingDown(boolean shuttingDown)
    {
        BaseLookup.shuttingDown = shuttingDown;
    }

    public static PrinterListChangesNotifier getPrinterListChangesNotifier()
    {
        return printerListChangesNotifier;
    }

    public static void printerConnected(Printer printer)
    {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            steno.debug(">>>Printer connection notification - " + printer);
            doPrinterConnect(printer);
        });
    }

    private static synchronized void doPrinterConnect(Printer printer)
    {
        CONNECTED_PRINTERS.add(printer);
    }

    public static void printerDisconnected(Printer printer)
    {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            steno.debug("<<<Printer disconnection notification - " + printer);
            doPrinterDisconnect(printer);
        });
    }

    private static synchronized void doPrinterDisconnect(Printer printer)
    {
        CONNECTED_PRINTERS.remove(printer);
    }

    public static ObservableList<Printer> getConnectedPrinters()
    {
        return CONNECTED_PRINTERS_UNMODIFIABLE;
    }
    
    public static void cameraConnected(CameraInfo camera)
    {
        CONNECTED_CAMS.add(camera);
    }
    
    public static void cameraDisconnected(CameraInfo camera)
    {
        CONNECTED_CAMS.remove(camera);
    }
    
    public static ObservableList<CameraInfo> getConnectedCameras()
    {
        return CONNECTED_CAMS_UNMODIFIABLE;
    }
    
    public static synchronized void retainAndAddUSBDirectories(File[] usbDirs) {
        MOUNTED_USB_DIRECTORIES.retainAll(usbDirs);
        for(File usbDir : usbDirs) {
            if(!MOUNTED_USB_DIRECTORIES.contains(usbDir)) {
                MOUNTED_USB_DIRECTORIES.add(usbDir);
            }
        }
    }

    public static Locale getDefaultApplicationLocale()
    {
        Locale appLocale = getLocaleFromTag(BaseConfiguration.getApplicationLocale());
        
        if (appLocale == null)
        {
            steno.debug("Default language tag is null - using \"en\" locale.");
            appLocale = Locale.ENGLISH;
        }
        
        return appLocale;
    }
    
    public static Locale getLocaleFromTag(String languageTag)
    {
        Locale appLocale;
        
        if (languageTag == null || languageTag.length() == 0)
        {
            steno.debug("Starting AutoMaker - language tag is null - using default locale.");
            appLocale = Locale.getDefault();
        } else
        {
            steno.debug("Starting AutoMaker - language tag is \"" + languageTag + "\"");
            String[] languageElements = languageTag.split("-");
            switch (languageElements.length)
            {
                case 1:
                    appLocale = new Locale(languageElements[0]);
                    break;
                case 2:
                    appLocale = new Locale(languageElements[0], languageElements[1]);
                    break;
                case 3:
                    appLocale = new Locale(languageElements[0], languageElements[1],
                            languageElements[2]);
                    break;
                default:
                    appLocale = Locale.getDefault();
                    break;
            }
        }
        
        return appLocale;
    }

    public static void setupDefaultValues()
    {
        setupDefaultValues(BaseConfiguration.getApplicationLogLevel(), 
                           getDefaultApplicationLocale(),
                           new ConsoleSystemNotificationManager());
    }

    public static Set<Locale> getAvailableLocales()
    {
        return I18n.getAvailableLocales();
    }

    public static Locale getApplicationLocale()
    {
        return I18n.getApplicationLocale();
    }

    public static void setApplicationLocale(Locale locale)
    {
        I18n.setApplicationLocale(locale);
    }

    public static void setupDefaultValues(LogLevel logLevel, Locale appLocale, SystemNotificationManager notificationManager)
    {
        StenographerFactory.changeAllLogLevels(logLevel);

        I18n.addBundlePrefix("UI_");
        I18n.addBundlePrefix("NoUI_");
        I18n.addSubDirectoryToSearch("Common");
        Path installPath = Paths.get(BaseConfiguration.getApplicationInstallDirectory(null)).normalize();
        I18n.addSubDirectoryToSearch(installPath.getFileName().toString());
        steno.info("Starting AutoMaker - loading resources for locale " + appLocale);
        I18n.loadMessages(installPath.getParent().toString(), appLocale);

        BaseLookup.setTaskExecutor(
                new LiveTaskExecutor());

        printerListChangesNotifier = new PrinterListChangesNotifier(BaseLookup.getConnectedPrinters());

        setSystemNotificationHandler(notificationManager);

        setSlicerMappings(SlicerMappingsContainer.getSlicerMappings());

        setPostProcessorOutputWriterFactory(LiveGCodeOutputWriter::new);
    }
}
