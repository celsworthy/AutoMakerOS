package celtech.configuration.fileRepresentation;

import celtech.configuration.UserPreferences;
import celtech.configuration.units.CurrencySymbol;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import libertysystems.stenographer.LogLevel;

/**
 *
 * @author Ian
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPreferenceFile
{

    private SlicerType slicerType = null;
    private boolean safetyFeaturesOn = true;
    private String languageTag = "";
    private boolean showTooltips = false;
    private LogLevel loggingLevel = LogLevel.INFO;
    private boolean advancedMode = false;
    private boolean firstUse = true;
    private boolean detectLoadedFilament = true;
    private CurrencySymbol currencySymbol = CurrencySymbol.POUND;
    private float currencyGBPToLocalMultiplier = 1;
    private boolean showDiagnostics = false;
    private boolean showGCode = true;
    private boolean showAdjustments = true;
    private boolean showSnapshot = true;
    private boolean autoGCodePreview = false;
    private boolean showMetricUnits = true;
    private boolean searchForRemoteCameras = true;
    private boolean loosePartSplitOnLoad = true;
    private boolean customPrinterEnabled = false;
    private PrinterType customPrinterType = PrinterType.ROBOX;
    private String customPrinterHead = HeadContainer.defaultHeadID;

    public String getLanguageTag()
    {
        return languageTag;
    }

    public void setLanguageTag(String languageTag)
    {
        this.languageTag = languageTag;
    }

    public SlicerType getSlicerType()
    {
        return slicerType;
    }

    public void setSlicerType(SlicerType slicerType)
    {
        this.slicerType = slicerType;
    }

    public boolean isSafetyFeaturesOn()
    {
        return safetyFeaturesOn;
    }

    public void setSafetyFeaturesOn(boolean value)
    {
        this.safetyFeaturesOn = value;
    }

    public boolean isShowTooltips()
    {
        return showTooltips;
    }

    public void setShowTooltips(boolean showTooltips)
    {
        this.showTooltips = showTooltips;
    }

    public LogLevel getLoggingLevel()
    {
        return loggingLevel;
    }

    public void setLoggingLevel(LogLevel loggingLevel)
    {
        this.loggingLevel = loggingLevel;
    }

    public boolean isAdvancedMode()
    {
        return advancedMode;
    }

    public void setAdvancedMode(boolean advancedMode)
    {
        this.advancedMode = advancedMode;
    }

    public boolean isFirstUse()
    {
        return firstUse;
    }

    public boolean isDetectLoadedFilament()
    {
        return detectLoadedFilament;
    }

    public void setFirstUse(boolean value)
    {
        this.firstUse = value;
    }

    public void setDetectLoadedFilament(boolean value)
    {
        this.detectLoadedFilament = value;
    }

    public CurrencySymbol getCurrencySymbol()
    {
        return currencySymbol;
    }

    public void setCurrencySymbol(CurrencySymbol currencySymbol)
    {
        this.currencySymbol = currencySymbol;
    }

    public float getCurrencyGBPToLocalMultiplier()
    {
        return currencyGBPToLocalMultiplier;
    }

    public void setCurrencyGBPToLocalMultiplier(float currencyGBPToLocalMultiplier)
    {
        this.currencyGBPToLocalMultiplier = currencyGBPToLocalMultiplier;
    }

    public boolean isShowDiagnostics()
    {
        return showDiagnostics;
    }

    public boolean isShowGCode()
    {
        return showGCode;
    }

    public boolean isShowAdjustments()
    {
        return showAdjustments;
    }

    public boolean isShowSnapshot()
    {
        return showSnapshot;
    }

    public boolean isAutoGCodePreview()
    {
        return autoGCodePreview;
    }

    public void setShowDiagnostics(boolean showDiagnostics)
    {
        this.showDiagnostics = showDiagnostics;
    }

    public void setShowGCode(boolean showGCode)
    {
        this.showGCode = showGCode;
    }

    public void setShowAdjustments(boolean showAdjustments)
    {
        this.showAdjustments = showAdjustments;
    }

    public void setShowSnapshot(boolean showSnapshot)
    {
        this.showSnapshot = showSnapshot;
    }

    public void setAutoGCodePreview(boolean autoGCodePreview)
    {
        this.autoGCodePreview = autoGCodePreview;
    }

    public boolean isShowMetricUnits()
    {
        return showMetricUnits;
    }

    public void setShowMetricUnits(boolean showMetricUnits)
    {
        this.showMetricUnits = showMetricUnits;
    }

    public boolean isSearchForRemoteCameras()
    {
        return searchForRemoteCameras;
    }

    public void setSearchForRemoteCameras(boolean searchForRemoteCameras)
    {
        this.searchForRemoteCameras = searchForRemoteCameras;
    }

    public boolean isLoosePartSplitOnLoad()
    {
        return loosePartSplitOnLoad;
    }

    public void setLoosePartSplitOnLoad(boolean loosePartSplitOnLoad)
    {
        this.loosePartSplitOnLoad = loosePartSplitOnLoad;
    }

    public boolean isCustomPrinterEnabled() 
    {
        return customPrinterEnabled;
    }

    public void setCustomPrinterEnabled(boolean customPrinterEnabled)
    {
        this.customPrinterEnabled = customPrinterEnabled;
    }
    
    public PrinterType getCustomPrinterType() 
    {
        return customPrinterType;
    }
    
    public void setCustomPrinterType(PrinterType customPrinterType) 
    {
        this.customPrinterType = customPrinterType;
    }

    public String getCustomPrinterHead() {
        return customPrinterHead;
    }

    public void setCustomPrinterHead(String customPrinterHead) 
    {
        this.customPrinterHead = customPrinterHead;
    }

    public void populateFromSettings(UserPreferences userPreferences)
    {
        setSlicerType(userPreferences.getSlicerType());
        setSafetyFeaturesOn(userPreferences.isSafetyFeaturesOn());
        setLanguageTag(userPreferences.getLanguageTag());
        setShowTooltips(userPreferences.isShowTooltips());
        setLoggingLevel(userPreferences.getLoggingLevel());
        setAdvancedMode(userPreferences.isAdvancedMode());
        setFirstUse(userPreferences.isFirstUse());
        setDetectLoadedFilament(userPreferences.getDetectLoadedFilament());
        setCurrencySymbol(userPreferences.getCurrencySymbol());
        setCurrencyGBPToLocalMultiplier(userPreferences.getcurrencyGBPToLocalMultiplier());
        setShowDiagnostics(userPreferences.getShowDiagnostics());
        setShowGCode(userPreferences.getShowGCode());
        setShowAdjustments(userPreferences.getShowAdjustments());
        setShowSnapshot(userPreferences.getShowSnapshot());
        setAutoGCodePreview(userPreferences.isAutoGCodePreview());
        setShowMetricUnits(userPreferences.isShowMetricUnits());
        setSearchForRemoteCameras(userPreferences.isSearchForRemoteCameras());
        setLoosePartSplitOnLoad(userPreferences.isLoosePartSplitOnLoad());
        setCustomPrinterEnabled(userPreferences.isCustomPrinterEnabled());
        setCustomPrinterType(userPreferences.getCustomPrinterType());
        setCustomPrinterHead(userPreferences.getCustomPrinterHead());
    }
}
