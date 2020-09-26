package celtech.configuration.fileRepresentation;

import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.modelcontrol.ItemState;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.SupportType;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelContainerProjectFile extends ProjectFile
{

    private int subVersion = 1;
    private int brimOverride = 0;
    private float fillDensityOverride = 0;
    private boolean fillDensityOverridenByUser = false;
    private boolean printSupportOverride = false;
    private SupportType printSupportTypeOverride = SupportType.MATERIAL_2;
    private boolean printRaft = false;
    private boolean spiralPrint = false;
    private String extruder0FilamentID;
    private String extruder1FilamentID;
    private String settingsName = BaseConfiguration.draftSettingsProfileName;
    private PrintQualityEnumeration printQuality = PrintQualityEnumeration.NORMAL;

    private Map<Integer, Set<Integer>> groupStructure = new HashMap<>();
    private Map<Integer, ItemState> groupState = new HashMap<>();

    public ModelContainerProjectFile()
    {
        setProjectType(ProjectFileTypeEnum.MODEL);
    }

    public String getSettingsName()
    {
        return settingsName;
    }

    public void setSettingsName(String settingsName)
    {
        this.settingsName = settingsName;
    }

    public PrintQualityEnumeration getPrintQuality()
    {
        return printQuality;
    }

    public void setPrintQuality(PrintQualityEnumeration printQuality)
    {
        this.printQuality = printQuality;
    }

    public int getBrimOverride()
    {
        return brimOverride;
    }

    public void setBrimOverride(int brimOverride)
    {
        this.brimOverride = brimOverride;
    }

    public float getFillDensityOverride()
    {
        return fillDensityOverride;
    }

    public void setFillDensityOverride(float fillDensityOverride)
    {
        this.fillDensityOverride = fillDensityOverride;
    }

    public boolean isFillDensityOverridenByUser()
    {
        return fillDensityOverridenByUser;
    }

    public void setFillDensityOverridenByUser(boolean fillDensityOverridenByUser) 
    {
        this.fillDensityOverridenByUser = fillDensityOverridenByUser;
    }
    public boolean getPrintSupportOverride()
    {
        return printSupportOverride;
    }

    public void setPrintSupportOverride(boolean printSupportOverride)
    {
        this.printSupportOverride = printSupportOverride;
    }

    public SupportType getPrintSupportTypeOverride()
    {
        return printSupportTypeOverride;
    }

    public void setPrintSupportTypeOverride(SupportType printSupportTypeOverride)
    {
        this.printSupportTypeOverride = printSupportTypeOverride;
    }

    public boolean getPrintRaft()
    {
        return printRaft;
    }

    public void setPrintRaft(boolean printRaft)
    {
        this.printRaft = printRaft;
    }

    public String getExtruder0FilamentID()
    {
        return extruder0FilamentID;
    }

    public void setExtruder0FilamentID(String extruder0FilamentID)
    {
        this.extruder0FilamentID = extruder0FilamentID;
    }

    public String getExtruder1FilamentID()
    {
        return extruder1FilamentID;
    }

    public void setExtruder1FilamentID(String extruder1FilamentID)
    {
        this.extruder1FilamentID = extruder1FilamentID;
    }

    public int getSubVersion()
    {
        return subVersion;
    }

    public void setSubVersion(int version)
    {
        this.subVersion = version;
    }

    public Map<Integer, Set<Integer>> getGroupStructure()
    {
        return groupStructure;
    }

    public void setGroupStructure(Map<Integer, Set<Integer>> groupStructure)
    {
        this.groupStructure = groupStructure;
    }

    public Map<Integer, ItemState> getGroupState()
    {
        return groupState;
    }

    public void setGroupState(Map<Integer, ItemState> groupState)
    {
        this.groupState = groupState;
    }

    public boolean getSpiralPrint()
    {
        return spiralPrint;
    }

    public void setSpiralPrint(boolean spiralPrint)
    {
        this.spiralPrint = spiralPrint;
    }

    public void populateFromProject(ModelContainerProject project)
    {
    }

    @Override
    public void implementationSpecificPopulate(Project project)
    {
        if (project instanceof ModelContainerProject)
        {
            ModelContainerProject mcp = (ModelContainerProject) project;
            extruder0FilamentID = mcp.getExtruder0FilamentProperty().get().getFilamentID();
            extruder1FilamentID = mcp.getExtruder1FilamentProperty().get().getFilamentID();
            settingsName = mcp.getPrinterSettings().getSettingsName();
            printQuality = mcp.getPrinterSettings().getPrintQuality();
            brimOverride = mcp.getPrinterSettings().getBrimOverride();
            fillDensityOverride = mcp.getPrinterSettings().getFillDensityOverride();
            fillDensityOverridenByUser = mcp.getPrinterSettings().isFillDensityChangedByUser();
            printSupportOverride = mcp.getPrinterSettings().getPrintSupportOverride();
            printSupportTypeOverride = mcp.getPrinterSettings().getPrintSupportTypeOverride();
            printRaft = mcp.getPrinterSettings().getRaftOverride();
            groupStructure = mcp.getGroupStructure();
            groupState = mcp.getGroupState();
            spiralPrint = mcp.getPrinterSettings().getSpiralPrintOverride();
        }
    }
}
