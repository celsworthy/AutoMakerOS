/*
 * Copyright 2015 CEL UK
 */
package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.configuration.fileRepresentation.SlicerMappingData;
import celtech.roboxbase.configuration.fileRepresentation.SupportType;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.apache.commons.io.FileUtils.readLines;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tony
 */
public class SlicerConfigWriterTest extends BaseEnvironmentConfiguredTest
{

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test 
    public void testOptionalOperatorConditionSucceeds() throws IOException {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        SlicerMappingData mappingData = new SlicerMappingData();
        mappingData.setDefaults(new ArrayList<>());
        mappingData.setMappingData(new HashMap<>());
        mappingData.getMappingData().put("supportAngle", "supportOverhangThreshold_degrees:?generateSupportMaterial=false->-1");
        configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("supportAngle=-1"));
    }
    
    @Test 
    public void testOptionalOperatorConditionFails() throws IOException {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        printerSettings.setPrintSupportTypeOverride(SupportType.MATERIAL_1);
        
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        SlicerMappingData mappingData = new SlicerMappingData();
        mappingData.setDefaults(new ArrayList<>());
        mappingData.setMappingData(new HashMap<>());
        mappingData.getMappingData().put("supportAngle", "supportOverhangThreshold_degrees:?generateSupportMaterial=false->-1");
        configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
        List<String> outputData = readLines(new File(destinationFile));
        assertFalse(outputData.contains("supportAngle=40"));
    }    
    
    @Test 
    public void testNoOutputOperatorConditionFails() throws IOException {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        printerSettings.setRaftOverride(true);
        
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        SlicerMappingData mappingData = new SlicerMappingData();
        mappingData.setDefaults(new ArrayList<>());
        mappingData.setMappingData(new HashMap<>());
        mappingData.getMappingData().put("raftInterfaceLinewidth", "400:?printRaft=false->|");
        configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("raftInterfaceLinewidth=400"));
    }      
    
    @Test 
    public void testNoOutputOperatorConditionSucceeds() throws IOException {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        printerSettings.setRaftOverride(false);
        
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        SlicerMappingData mappingData = new SlicerMappingData();
        mappingData.setDefaults(new ArrayList<>());
        mappingData.setMappingData(new HashMap<>());
        mappingData.getMappingData().put("raftInterfaceLinewidth", "400:?printRaft=false->|");
        configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(! outputData.contains("raftInterfaceLinewidth"));
    }      
        
    
    @Test
    public void testGenerateConfigForRaftOnCuraDraft() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        printerSettings.setRaftOverride(true);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("raftBaseThickness=300"));
        assertTrue(outputData.contains("raftInterfaceThickness=280"));
    }

    @Test
    public void testGenerateConfigForRaftOnCuraNormal() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
        printerSettings.setRaftOverride(true);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("raftBaseThickness=300"));
        assertTrue(outputData.contains("raftInterfaceThickness=280"));
    }

    @Test
    public void testGenerateConfigForRaftOnCuraFine() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
        printerSettings.setRaftOverride(true);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("raftBaseThickness=300"));
        assertTrue(outputData.contains("raftInterfaceThickness=280"));
    }

    @Test
    public void testGenerateConfigForNoRaftOnCuraDraft() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
        printerSettings.setRaftOverride(false);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(!outputData.contains("raftBaseThickness"));
        assertTrue(!outputData.contains("raftInterfaceThickness"));
    }

    @Test
    public void testGenerateConfigForNoRaftOnCuraNormal() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
        printerSettings.setRaftOverride(false);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(!outputData.contains("raftBaseThickness"));
        assertTrue(!outputData.contains("raftInterfaceThickness"));
    }

    @Test
    public void testGenerateConfigForNoRaftOnCuraFine() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
        printerSettings.setRaftOverride(false);
        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(!outputData.contains("raftBaseThickness"));
        assertTrue(!outputData.contains("raftInterfaceThickness"));
    }

//    @Test
//    public void testGenerateConfigForRaftOnSlic3rDraft() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
//        printerSettings.setRaftOverride(true);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        for (String outputData1 : outputData)
//        {
//            System.out.println(outputData1);
//        }
//        assertTrue(outputData.contains("raft_layers = 2"));
//        assertTrue(outputData.contains("support_material_interface_layers = 1"));
//    }
//
//    @Test
//    public void testGenerateConfigForNoRaftOnSlic3rDraft() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
//        printerSettings.setRaftOverride(false);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        assertTrue(outputData.contains("raft_layers = 0"));
//    }
//
//    @Test
//    public void testGenerateConfigForRaftOnSlic3rNormal() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
//        printerSettings.setRaftOverride(true);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        assertTrue(outputData.contains("raft_layers = 2"));
//    }
//
//    @Test
//    public void testGenerateConfigForNoRaftOnSlic3rNormal() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
//        printerSettings.setRaftOverride(false);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        assertTrue(outputData.contains("raft_layers = 0"));
//    }
//
//    @Test
//    public void testGenerateConfigForRaftOnSlic3rFine() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
//        printerSettings.setRaftOverride(true);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        assertTrue(outputData.contains("raft_layers = 3"));
//    }
//
//    @Test
//    public void testGenerateConfigForNoRaftOnSlic3rFine() throws IOException
//    {
//        String TEMPFILENAME = "output.roboxprofile";
//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
//            SlicerType.Slic3r);
//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
//        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
//        printerSettings.setRaftOverride(false);
//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
//            + TEMPFILENAME;
//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
//        List<String> outputData = readLines(new File(destinationFile));
//        assertTrue(outputData.contains("raft_layers = 0"));
//    }

    @Test
    public void testGenerateConfigForSparseInfillOffCuraFine() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
        printerSettings.setFillDensityOverride(0);
        printerSettings.setFillDensityChangedByUser(true);

        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("sparseInfillLineDistance=-1"));
    }

    @Test
    public void testGenerateConfigForSparseInfillOnCuraFine() throws IOException
    {
        String TEMPFILENAME = "output.roboxprofile";
        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
            SlicerType.Cura);
        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
        printerSettings.setFillDensityOverride(0.5f);
        printerSettings.setFillDensityChangedByUser(true);

        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
            + TEMPFILENAME;
        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
        List<String> outputData = readLines(new File(destinationFile));
        assertTrue(outputData.contains("sparseInfillLineDistance=800"));
    }

}
