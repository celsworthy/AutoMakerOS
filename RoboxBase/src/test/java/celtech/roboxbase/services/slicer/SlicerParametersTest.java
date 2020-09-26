/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.roboxbase.services.slicer;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ian
 */
public class SlicerParametersTest
{
    
    public SlicerParametersTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }
    
    /**
     * Test of clone method, of class SlicerParameters.
     */
//    @Test
//    public void testClone()
//    {
//        System.out.println("clone");
//        SlicerParameters instance = new SlicerParameters();
//        SlicerParameters result = instance.clone();
//        assertEquals(instance, result);
//    }
//
//    /**
//     * Test of setFilament_diameter method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFilament_diameter()
//    {
//        System.out.println("setFilament_diameter");
//        float value = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFilament_diameter(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFilament_diameter method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFilament_diameter()
//    {
//        System.out.println("getFilament_diameter");
//        SlicerParameters instance = new SlicerParameters();
//        float expResult = 0.0F;
//        float result = instance.getFilament_diameter();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of filament_diameterProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testFilament_diameterProperty()
//    {
//        System.out.println("filament_diameterProperty");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.filament_diameterProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPrint_center method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetPrint_center()
//    {
//        System.out.println("getPrint_center");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getPrint_center();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPrint_center method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPrint_center()
//    {
//        System.out.println("setPrint_center");
//        StringProperty print_center = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPrint_center(print_center);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_restart_extra_toolchange method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_restart_extra_toolchange()
//    {
//        System.out.println("getRetract_restart_extra_toolchange");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getRetract_restart_extra_toolchange();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_restart_extra_toolchange method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_restart_extra_toolchange()
//    {
//        System.out.println("setRetract_restart_extra_toolchange");
//        ObservableList<FloatProperty> retract_restart_extra_toolchange = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_restart_extra_toolchange(retract_restart_extra_toolchange);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBed_size method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetBed_size()
//    {
//        System.out.println("getBed_size");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<IntegerProperty> expResult = null;
//        ObservableList<IntegerProperty> result = instance.getBed_size();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBed_size method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBed_size()
//    {
//        System.out.println("setBed_size");
//        ObservableList<IntegerProperty> bed_size = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBed_size(bed_size);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDuplicate_grid method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetDuplicate_grid()
//    {
//        System.out.println("getDuplicate_grid");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getDuplicate_grid();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDuplicate_grid method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetDuplicate_grid()
//    {
//        System.out.println("setDuplicate_grid");
//        StringProperty duplicate_grid = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setDuplicate_grid(duplicate_grid);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getZ_offset method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetZ_offset()
//    {
//        System.out.println("getZ_offset");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getZ_offset();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setZ_offset method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetZ_offset()
//    {
//        System.out.println("setZ_offset");
//        FloatProperty z_offset = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setZ_offset(z_offset);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGcode_flavor method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetGcode_flavor()
//    {
//        System.out.println("getGcode_flavor");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getGcode_flavor();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGcode_flavor method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetGcode_flavor()
//    {
//        System.out.println("setGcode_flavor");
//        StringProperty gcode_flavor = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setGcode_flavor(gcode_flavor);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getUse_relative_e_distances method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetUse_relative_e_distances()
//    {
//        System.out.println("getUse_relative_e_distances");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getUse_relative_e_distances();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setUse_relative_e_distances method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetUse_relative_e_distances()
//    {
//        System.out.println("setUse_relative_e_distances");
//        boolean use_relative_e_distances = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setUse_relative_e_distances(use_relative_e_distances);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getOutput_nozzle_control method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetOutput_nozzle_control()
//    {
//        System.out.println("getOutput_nozzle_control");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getOutput_nozzle_control();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setOutput_nozzle_control method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetOutput_nozzle_control()
//    {
//        System.out.println("setOutput_nozzle_control");
//        boolean output_nozzle_control = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setOutput_nozzle_control(output_nozzle_control);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getVibration_limit method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetVibration_limit()
//    {
//        System.out.println("getVibration_limit");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getVibration_limit();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setVibration_limit method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetVibration_limit()
//    {
//        System.out.println("setVibration_limit");
//        IntegerProperty vibration_limit = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setVibration_limit(vibration_limit);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getStart_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetStart_gcode()
//    {
//        System.out.println("getStart_gcode");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getStart_gcode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setStart_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetStart_gcode()
//    {
//        System.out.println("setStart_gcode");
//        String start_gcode = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.setStart_gcode(start_gcode);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEnd_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetEnd_gcode()
//    {
//        System.out.println("getEnd_gcode");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getEnd_gcode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setEnd_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetEnd_gcode()
//    {
//        System.out.println("setEnd_gcode");
//        String end_gcode = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.setEnd_gcode(end_gcode);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLayer_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetLayer_gcode()
//    {
//        System.out.println("getLayer_gcode");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getLayer_gcode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLayer_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetLayer_gcode()
//    {
//        System.out.println("setLayer_gcode");
//        StringProperty layer_gcode = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setLayer_gcode(layer_gcode);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getToolchange_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetToolchange_gcode()
//    {
//        System.out.println("getToolchange_gcode");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getToolchange_gcode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setToolchange_gcode method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetToolchange_gcode()
//    {
//        System.out.println("setToolchange_gcode");
//        StringProperty toolchange_gcode = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setToolchange_gcode(toolchange_gcode);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of perimeter_nozzleProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testPerimeter_nozzleProperty()
//    {
//        System.out.println("perimeter_nozzleProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.perimeter_nozzleProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeter_nozzle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeter_nozzle()
//    {
//        System.out.println("setPerimeter_nozzle");
//        int perimeter_nozzle = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeter_nozzle(perimeter_nozzle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of infill_nozzleProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testInfill_nozzleProperty()
//    {
//        System.out.println("infill_nozzleProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.infill_nozzleProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_nozzle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_nozzle()
//    {
//        System.out.println("setInfill_nozzle");
//        int infill_nozzle = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_nozzle(infill_nozzle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_nozzleProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_nozzleProperty()
//    {
//        System.out.println("support_material_nozzleProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_nozzleProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_nozzle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_nozzle()
//    {
//        System.out.println("setSupport_material_nozzle");
//        int support_material_nozzle = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_nozzle(support_material_nozzle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_interface_nozzleProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_interface_nozzleProperty()
//    {
//        System.out.println("support_material_interface_nozzleProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_interface_nozzleProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_interface_nozzle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_interface_nozzle()
//    {
//        System.out.println("setSupport_material_interface_nozzle");
//        int support_material_interface_nozzle = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_interface_nozzle(support_material_interface_nozzle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_lift method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_lift()
//    {
//        System.out.println("getRetract_lift");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getRetract_lift();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_lift method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_lift()
//    {
//        System.out.println("setRetract_lift");
//        ObservableList<FloatProperty> retract_lift = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_lift(retract_lift);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_restart_extra method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_restart_extra()
//    {
//        System.out.println("getRetract_restart_extra");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getRetract_restart_extra();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_restart_extra method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_restart_extra()
//    {
//        System.out.println("setRetract_restart_extra");
//        ObservableList<FloatProperty> retract_restart_extra = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_restart_extra(retract_restart_extra);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_before_travel method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_before_travel()
//    {
//        System.out.println("getRetract_before_travel");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getRetract_before_travel();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_before_travel method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_before_travel()
//    {
//        System.out.println("setRetract_before_travel");
//        ObservableList<FloatProperty> retract_before_travel = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_before_travel(retract_before_travel);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_layer_change method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_layer_change()
//    {
//        System.out.println("getRetract_layer_change");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getRetract_layer_change();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_layer_change method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_layer_change()
//    {
//        System.out.println("setRetract_layer_change");
//        boolean retract_layer_change = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_layer_change(retract_layer_change);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getWipe method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetWipe()
//    {
//        System.out.println("getWipe");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<IntegerProperty> expResult = null;
//        ObservableList<IntegerProperty> result = instance.getWipe();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setWipe method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetWipe()
//    {
//        System.out.println("setWipe");
//        ObservableList<IntegerProperty> wipe = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setWipe(wipe);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNozzle_diameter method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetNozzle_diameter()
//    {
//        System.out.println("getNozzle_diameter");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getNozzle_diameter();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setNozzle_diameter method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetNozzle_diameter()
//    {
//        System.out.println("setNozzle_diameter");
//        ObservableList<FloatProperty> nozzle_diameter = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setNozzle_diameter(nozzle_diameter);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPerimeter_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetPerimeter_acceleration()
//    {
//        System.out.println("getPerimeter_acceleration");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getPerimeter_acceleration();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeter_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeter_acceleration()
//    {
//        System.out.println("setPerimeter_acceleration");
//        IntegerProperty perimeter_acceleration = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeter_acceleration(perimeter_acceleration);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInfill_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetInfill_acceleration()
//    {
//        System.out.println("getInfill_acceleration");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getInfill_acceleration();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_acceleration()
//    {
//        System.out.println("setInfill_acceleration");
//        IntegerProperty infill_acceleration = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_acceleration(infill_acceleration);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBridge_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetBridge_acceleration()
//    {
//        System.out.println("getBridge_acceleration");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getBridge_acceleration();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBridge_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBridge_acceleration()
//    {
//        System.out.println("setBridge_acceleration");
//        IntegerProperty bridge_acceleration = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBridge_acceleration(bridge_acceleration);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDefault_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetDefault_acceleration()
//    {
//        System.out.println("getDefault_acceleration");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getDefault_acceleration();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDefault_acceleration method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetDefault_acceleration()
//    {
//        System.out.println("setDefault_acceleration");
//        IntegerProperty default_acceleration = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setDefault_acceleration(default_acceleration);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of retract_lengthProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testRetract_lengthProperty()
//    {
//        System.out.println("retract_lengthProperty");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.retract_lengthProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_length method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_length()
//    {
//        System.out.println("setRetract_length");
//        ObservableList<FloatProperty> retract_length = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_length(retract_length);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of retract_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testRetract_speedProperty()
//    {
//        System.out.println("retract_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<IntegerProperty> expResult = null;
//        ObservableList<IntegerProperty> result = instance.retract_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_speed()
//    {
//        System.out.println("setRetract_speed");
//        ObservableList<IntegerProperty> retract_speed = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_speed(retract_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNozzle_ejection_volume method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetNozzle_ejection_volume()
//    {
//        System.out.println("getNozzle_ejection_volume");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getNozzle_ejection_volume();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNozzle_partial_b_minimum method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetNozzle_partial_b_minimum()
//    {
//        System.out.println("getNozzle_partial_b_minimum");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getNozzle_partial_b_minimum();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNozzle_wipe_volume method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetNozzle_wipe_volume()
//    {
//        System.out.println("getNozzle_wipe_volume");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getNozzle_wipe_volume();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFan_always_on method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFan_always_on()
//    {
//        System.out.println("getFan_always_on");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getFan_always_on();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFan_always_on method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFan_always_on()
//    {
//        System.out.println("setFan_always_on");
//        BooleanProperty fan_always_on = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFan_always_on(fan_always_on);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getCooling method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetCooling()
//    {
//        System.out.println("getCooling");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getCooling();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setCooling method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetCooling()
//    {
//        System.out.println("setCooling");
//        BooleanProperty cooling = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setCooling(cooling);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMax_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetMax_fan_speed()
//    {
//        System.out.println("getMax_fan_speed");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getMax_fan_speed();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setMax_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetMax_fan_speed()
//    {
//        System.out.println("setMax_fan_speed");
//        IntegerProperty max_fan_speed = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setMax_fan_speed(max_fan_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMin_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetMin_fan_speed()
//    {
//        System.out.println("getMin_fan_speed");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getMin_fan_speed();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setMin_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetMin_fan_speed()
//    {
//        System.out.println("setMin_fan_speed");
//        IntegerProperty min_fan_speed = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setMin_fan_speed(min_fan_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBridge_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetBridge_fan_speed()
//    {
//        System.out.println("getBridge_fan_speed");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getBridge_fan_speed();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBridge_fan_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBridge_fan_speed()
//    {
//        System.out.println("setBridge_fan_speed");
//        IntegerProperty bridge_fan_speed = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBridge_fan_speed(bridge_fan_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDisable_fan_first_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetDisable_fan_first_layers()
//    {
//        System.out.println("getDisable_fan_first_layers");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getDisable_fan_first_layers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDisable_fan_first_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetDisable_fan_first_layers()
//    {
//        System.out.println("setDisable_fan_first_layers");
//        IntegerProperty disable_fan_first_layers = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setDisable_fan_first_layers(disable_fan_first_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFan_below_layer_time method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFan_below_layer_time()
//    {
//        System.out.println("getFan_below_layer_time");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getFan_below_layer_time();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFan_below_layer_time method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFan_below_layer_time()
//    {
//        System.out.println("setFan_below_layer_time");
//        IntegerProperty fan_below_layer_time = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFan_below_layer_time(fan_below_layer_time);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSlowdown_below_layer_time method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSlowdown_below_layer_time()
//    {
//        System.out.println("getSlowdown_below_layer_time");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSlowdown_below_layer_time();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSlowdown_below_layer_time method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSlowdown_below_layer_time()
//    {
//        System.out.println("setSlowdown_below_layer_time");
//        IntegerProperty slowdown_below_layer_time = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSlowdown_below_layer_time(slowdown_below_layer_time);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMin_print_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetMin_print_speed()
//    {
//        System.out.println("getMin_print_speed");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getMin_print_speed();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setMin_print_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetMin_print_speed()
//    {
//        System.out.println("setMin_print_speed");
//        IntegerProperty min_print_speed = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setMin_print_speed(min_print_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fill_densityProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testFill_densityProperty()
//    {
//        System.out.println("fill_densityProperty");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.fill_densityProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFill_density method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFill_density()
//    {
//        System.out.println("setFill_density");
//        float fill_density = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFill_density(fill_density);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of fill_patternProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testFill_patternProperty()
//    {
//        System.out.println("fill_patternProperty");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.fill_patternProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFill_pattern method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFill_pattern()
//    {
//        System.out.println("setFill_pattern");
//        String fill_pattern = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFill_pattern(fill_pattern);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of infill_every_layersProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testInfill_every_layersProperty()
//    {
//        System.out.println("infill_every_layersProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.infill_every_layersProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_every_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_every_layers()
//    {
//        System.out.println("setInfill_every_layers");
//        int infill_every_layers = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_every_layers(infill_every_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInfill_only_where_needed method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetInfill_only_where_needed()
//    {
//        System.out.println("getInfill_only_where_needed");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getInfill_only_where_needed();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_only_where_needed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_only_where_needed()
//    {
//        System.out.println("setInfill_only_where_needed");
//        boolean infill_only_where_needed = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_only_where_needed(infill_only_where_needed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSolid_infill_every_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSolid_infill_every_layers()
//    {
//        System.out.println("getSolid_infill_every_layers");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSolid_infill_every_layers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSolid_infill_every_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSolid_infill_every_layers()
//    {
//        System.out.println("setSolid_infill_every_layers");
//        int solid_infill_every_layers = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSolid_infill_every_layers(solid_infill_every_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFill_angle method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFill_angle()
//    {
//        System.out.println("getFill_angle");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getFill_angle();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFill_angle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFill_angle()
//    {
//        System.out.println("setFill_angle");
//        IntegerProperty fill_angle = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFill_angle(fill_angle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSolid_infill_below_area method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSolid_infill_below_area()
//    {
//        System.out.println("getSolid_infill_below_area");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSolid_infill_below_area();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSolid_infill_below_area method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSolid_infill_below_area()
//    {
//        System.out.println("setSolid_infill_below_area");
//        IntegerProperty solid_infill_below_area = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSolid_infill_below_area(solid_infill_below_area);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getOnly_retract_when_crossing_perimeters method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetOnly_retract_when_crossing_perimeters()
//    {
//        System.out.println("getOnly_retract_when_crossing_perimeters");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getOnly_retract_when_crossing_perimeters();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInfill_first method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetInfill_first()
//    {
//        System.out.println("getInfill_first");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getInfill_first();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of perimeter_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testPerimeter_speedProperty()
//    {
//        System.out.println("perimeter_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.perimeter_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeter_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeter_speed()
//    {
//        System.out.println("setPerimeter_speed");
//        int perimeter_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeter_speed(perimeter_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of small_perimeter_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSmall_perimeter_speedProperty()
//    {
//        System.out.println("small_perimeter_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.small_perimeter_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSmall_perimeter_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSmall_perimeter_speed()
//    {
//        System.out.println("setSmall_perimeter_speed");
//        int small_perimeter_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSmall_perimeter_speed(small_perimeter_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of external_perimeter_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testExternal_perimeter_speedProperty()
//    {
//        System.out.println("external_perimeter_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.external_perimeter_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setExternal_perimeter_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetExternal_perimeter_speed()
//    {
//        System.out.println("setExternal_perimeter_speed");
//        int external_perimeter_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setExternal_perimeter_speed(external_perimeter_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of infill_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testInfill_speedProperty()
//    {
//        System.out.println("infill_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.infill_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_speed()
//    {
//        System.out.println("setInfill_speed");
//        int infill_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_speed(infill_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of solid_infill_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSolid_infill_speedProperty()
//    {
//        System.out.println("solid_infill_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.solid_infill_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSolid_infill_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSolid_infill_speed()
//    {
//        System.out.println("setSolid_infill_speed");
//        int solid_infill_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSolid_infill_speed(solid_infill_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of top_solid_infill_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testTop_solid_infill_speedProperty()
//    {
//        System.out.println("top_solid_infill_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.top_solid_infill_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTop_solid_infill_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetTop_solid_infill_speed()
//    {
//        System.out.println("setTop_solid_infill_speed");
//        int top_solid_infill_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setTop_solid_infill_speed(top_solid_infill_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_speedProperty()
//    {
//        System.out.println("support_material_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_speed()
//    {
//        System.out.println("setSupport_material_speed");
//        int support_material_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_speed(support_material_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of bridge_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testBridge_speedProperty()
//    {
//        System.out.println("bridge_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.bridge_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBridge_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBridge_speed()
//    {
//        System.out.println("setBridge_speed");
//        int bridge_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBridge_speed(bridge_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of gap_fill_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testGap_fill_speedProperty()
//    {
//        System.out.println("gap_fill_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.gap_fill_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGap_fill_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetGap_fill_speed()
//    {
//        System.out.println("setGap_fill_speed");
//        int gap_fill_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setGap_fill_speed(gap_fill_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of travel_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testTravel_speedProperty()
//    {
//        System.out.println("travel_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.travel_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTravel_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetTravel_speed()
//    {
//        System.out.println("setTravel_speed");
//        int travel_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setTravel_speed(travel_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of first_layer_speedProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testFirst_layer_speedProperty()
//    {
//        System.out.println("first_layer_speedProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.first_layer_speedProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFirst_layer_speed method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFirst_layer_speed()
//    {
//        System.out.println("setFirst_layer_speed");
//        int first_layer_speed = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFirst_layer_speed(first_layer_speed);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_thresholdProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_thresholdProperty()
//    {
//        System.out.println("support_material_thresholdProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_thresholdProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_threshold method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_threshold()
//    {
//        System.out.println("setSupport_material_threshold");
//        int support_material_threshold = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_threshold(support_material_threshold);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_enforce_layersProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_enforce_layersProperty()
//    {
//        System.out.println("support_material_enforce_layersProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_enforce_layersProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_enforce_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_enforce_layers()
//    {
//        System.out.println("setSupport_material_enforce_layers");
//        int support_material_enforce_layers = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_enforce_layers(support_material_enforce_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRaft_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRaft_layers()
//    {
//        System.out.println("getRaft_layers");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getRaft_layers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRaft_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRaft_layers()
//    {
//        System.out.println("setRaft_layers");
//        IntegerProperty raft_layers = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRaft_layers(raft_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_patternProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_patternProperty()
//    {
//        System.out.println("support_material_patternProperty");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.support_material_patternProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_pattern method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_pattern()
//    {
//        System.out.println("setSupport_material_pattern");
//        String support_material_pattern = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_pattern(support_material_pattern);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_spacingProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_spacingProperty()
//    {
//        System.out.println("support_material_spacingProperty");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.support_material_spacingProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_spacing method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_spacing()
//    {
//        System.out.println("setSupport_material_spacing");
//        float support_material_spacing = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_spacing(support_material_spacing);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_material_angleProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_material_angleProperty()
//    {
//        System.out.println("support_material_angleProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.support_material_angleProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_angle method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_angle()
//    {
//        System.out.println("setSupport_material_angle");
//        int support_material_angle = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_angle(support_material_angle);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSupport_material_interface_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSupport_material_interface_layers()
//    {
//        System.out.println("getSupport_material_interface_layers");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSupport_material_interface_layers();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_interface_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_interface_layers()
//    {
//        System.out.println("setSupport_material_interface_layers");
//        IntegerProperty support_material_interface_layers = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_interface_layers(support_material_interface_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSupport_material_interface_spacing method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSupport_material_interface_spacing()
//    {
//        System.out.println("getSupport_material_interface_spacing");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSupport_material_interface_spacing();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material_interface_spacing method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material_interface_spacing()
//    {
//        System.out.println("setSupport_material_interface_spacing");
//        IntegerProperty support_material_interface_spacing = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material_interface_spacing(support_material_interface_spacing);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLayer_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetLayer_height()
//    {
//        System.out.println("getLayer_height");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getLayer_height();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setLayer_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetLayer_height()
//    {
//        System.out.println("setLayer_height");
//        FloatProperty layer_height = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setLayer_height(layer_height);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of support_materialProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSupport_materialProperty()
//    {
//        System.out.println("support_materialProperty");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.support_materialProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSupport_material method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSupport_material()
//    {
//        System.out.println("setSupport_material");
//        boolean support_material = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSupport_material(support_material);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAvoid_crossing_perimeters method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetAvoid_crossing_perimeters()
//    {
//        System.out.println("getAvoid_crossing_perimeters");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getAvoid_crossing_perimeters();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setAvoid_crossing_perimeters method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetAvoid_crossing_perimeters()
//    {
//        System.out.println("setAvoid_crossing_perimeters");
//        boolean avoid_crossing_perimeters = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setAvoid_crossing_perimeters(avoid_crossing_perimeters);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of bottom_solid_layersProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testBottom_solid_layersProperty()
//    {
//        System.out.println("bottom_solid_layersProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.bottom_solid_layersProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBottom_solid_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBottom_solid_layers()
//    {
//        System.out.println("setBottom_solid_layers");
//        int bottom_solid_layers = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBottom_solid_layers(bottom_solid_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBridge_flow_ratio method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetBridge_flow_ratio()
//    {
//        System.out.println("getBridge_flow_ratio");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getBridge_flow_ratio();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBridge_flow_ratio method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBridge_flow_ratio()
//    {
//        System.out.println("setBridge_flow_ratio");
//        IntegerProperty bridge_flow_ratio = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBridge_flow_ratio(bridge_flow_ratio);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getBrim_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetBrim_width()
//    {
//        System.out.println("getBrim_width");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getBrim_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setBrim_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetBrim_width()
//    {
//        System.out.println("setBrim_width");
//        IntegerProperty brim_width = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setBrim_width(brim_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getComplete_objects method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetComplete_objects()
//    {
//        System.out.println("getComplete_objects");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getComplete_objects();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDuplicate method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetDuplicate()
//    {
//        System.out.println("getDuplicate");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getDuplicate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDuplicate method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetDuplicate()
//    {
//        System.out.println("setDuplicate");
//        IntegerProperty duplicate = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setDuplicate(duplicate);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getDuplicate_distance method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetDuplicate_distance()
//    {
//        System.out.println("getDuplicate_distance");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getDuplicate_distance();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setDuplicate_distance method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetDuplicate_distance()
//    {
//        System.out.println("setDuplicate_distance");
//        IntegerProperty duplicate_distance = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setDuplicate_distance(duplicate_distance);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExternal_perimeters_first method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExternal_perimeters_first()
//    {
//        System.out.println("getExternal_perimeters_first");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getExternal_perimeters_first();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExtra_perimeters method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExtra_perimeters()
//    {
//        System.out.println("getExtra_perimeters");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getExtra_perimeters();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExtruder_clearance_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExtruder_clearance_height()
//    {
//        System.out.println("getExtruder_clearance_height");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getExtruder_clearance_height();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setExtruder_clearance_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetExtruder_clearance_height()
//    {
//        System.out.println("setExtruder_clearance_height");
//        IntegerProperty extruder_clearance_height = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setExtruder_clearance_height(extruder_clearance_height);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExtruder_clearance_radius method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExtruder_clearance_radius()
//    {
//        System.out.println("getExtruder_clearance_radius");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getExtruder_clearance_radius();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setExtruder_clearance_radius method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetExtruder_clearance_radius()
//    {
//        System.out.println("setExtruder_clearance_radius");
//        IntegerProperty extruder_clearance_radius = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setExtruder_clearance_radius(extruder_clearance_radius);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExtrusion_axis method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExtrusion_axis()
//    {
//        System.out.println("getExtrusion_axis");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getExtrusion_axis();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setExtrusion_axis method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetExtrusion_axis()
//    {
//        System.out.println("setExtrusion_axis");
//        StringProperty extrusion_axis = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setExtrusion_axis(extrusion_axis);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getExtrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetExtrusion_width()
//    {
//        System.out.println("getExtrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getExtrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSupport_material_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSupport_material_extrusion_width()
//    {
//        System.out.println("getSupport_material_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getSupport_material_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFirst_layer_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFirst_layer_extrusion_width()
//    {
//        System.out.println("getFirst_layer_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getFirst_layer_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFirst_layer_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFirst_layer_extrusion_width()
//    {
//        System.out.println("setFirst_layer_extrusion_width");
//        StringProperty first_layer_extrusion_width = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFirst_layer_extrusion_width(first_layer_extrusion_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getFirst_layer_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetFirst_layer_height()
//    {
//        System.out.println("getFirst_layer_height");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getFirst_layer_height();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setFirst_layer_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetFirst_layer_height()
//    {
//        System.out.println("setFirst_layer_height");
//        FloatProperty first_layer_height = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setFirst_layer_height(first_layer_height);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getG0 method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetG0()
//    {
//        System.out.println("getG0");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getG0();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setG0 method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetG0()
//    {
//        System.out.println("setG0");
//        IntegerProperty g0 = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setG0(g0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGcode_arcs method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetGcode_arcs()
//    {
//        System.out.println("getGcode_arcs");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getGcode_arcs();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setGcode_arcs method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetGcode_arcs()
//    {
//        System.out.println("setGcode_arcs");
//        IntegerProperty gcode_arcs = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setGcode_arcs(gcode_arcs);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getGcode_comments method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetGcode_comments()
//    {
//        System.out.println("getGcode_comments");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getGcode_comments();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInfill_extruder method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetInfill_extruder()
//    {
//        System.out.println("getInfill_extruder");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getInfill_extruder();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_extruder method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_extruder()
//    {
//        System.out.println("setInfill_extruder");
//        IntegerProperty infill_extruder = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_extruder(infill_extruder);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInfill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetInfill_extrusion_width()
//    {
//        System.out.println("getInfill_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getInfill_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setInfill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetInfill_extrusion_width()
//    {
//        System.out.println("setInfill_extrusion_width");
//        float infill_extrusion_width = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setInfill_extrusion_width(infill_extrusion_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getMin_skirt_length method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetMin_skirt_length()
//    {
//        System.out.println("getMin_skirt_length");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getMin_skirt_length();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setMin_skirt_length method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetMin_skirt_length()
//    {
//        System.out.println("setMin_skirt_length");
//        IntegerProperty min_skirt_length = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setMin_skirt_length(min_skirt_length);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNotes method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetNotes()
//    {
//        System.out.println("getNotes");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getNotes();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setNotes method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetNotes()
//    {
//        System.out.println("setNotes");
//        StringProperty notes = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setNotes(notes);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getOutput_filename_format method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetOutput_filename_format()
//    {
//        System.out.println("getOutput_filename_format");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getOutput_filename_format();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setOutput_filename_format method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetOutput_filename_format()
//    {
//        System.out.println("setOutput_filename_format");
//        StringProperty output_filename_format = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setOutput_filename_format(output_filename_format);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPerimeter_extruder method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetPerimeter_extruder()
//    {
//        System.out.println("getPerimeter_extruder");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getPerimeter_extruder();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeter_extruder method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeter_extruder()
//    {
//        System.out.println("setPerimeter_extruder");
//        IntegerProperty perimeter_extruder = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeter_extruder(perimeter_extruder);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPerimeter_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetPerimeter_extrusion_width()
//    {
//        System.out.println("getPerimeter_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getPerimeter_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeter_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeter_extrusion_width()
//    {
//        System.out.println("setPerimeter_extrusion_width");
//        float perimeter_extrusion_width = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeter_extrusion_width(perimeter_extrusion_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of perimetersProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testPerimetersProperty()
//    {
//        System.out.println("perimetersProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.perimetersProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPerimeters method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPerimeters()
//    {
//        System.out.println("setPerimeters");
//        int perimeters = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPerimeters(perimeters);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPost_process method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetPost_process()
//    {
//        System.out.println("getPost_process");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getPost_process();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPost_process method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetPost_process()
//    {
//        System.out.println("setPost_process");
//        StringProperty post_process = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setPost_process(post_process);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRandomize_start method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRandomize_start()
//    {
//        System.out.println("getRandomize_start");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.getRandomize_start();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getResolution method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetResolution()
//    {
//        System.out.println("getResolution");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getResolution();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setResolution method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetResolution()
//    {
//        System.out.println("setResolution");
//        IntegerProperty resolution = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setResolution(resolution);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRetract_length_toolchange method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRetract_length_toolchange()
//    {
//        System.out.println("getRetract_length_toolchange");
//        SlicerParameters instance = new SlicerParameters();
//        ObservableList<FloatProperty> expResult = null;
//        ObservableList<FloatProperty> result = instance.getRetract_length_toolchange();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRetract_length_toolchange method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRetract_length_toolchange()
//    {
//        System.out.println("setRetract_length_toolchange");
//        ObservableList<FloatProperty> retract_length_toolchange = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRetract_length_toolchange(retract_length_toolchange);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRotate method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetRotate()
//    {
//        System.out.println("getRotate");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getRotate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setRotate method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetRotate()
//    {
//        System.out.println("setRotate");
//        IntegerProperty rotate = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setRotate(rotate);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getScale method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetScale()
//    {
//        System.out.println("getScale");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getScale();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setScale method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetScale()
//    {
//        System.out.println("setScale");
//        IntegerProperty scale = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setScale(scale);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSkirt_distance method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSkirt_distance()
//    {
//        System.out.println("getSkirt_distance");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSkirt_distance();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSkirt_distance method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSkirt_distance()
//    {
//        System.out.println("setSkirt_distance");
//        IntegerProperty skirt_distance = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSkirt_distance(skirt_distance);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSkirt_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSkirt_height()
//    {
//        System.out.println("getSkirt_height");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSkirt_height();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSkirt_height method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSkirt_height()
//    {
//        System.out.println("setSkirt_height");
//        IntegerProperty skirt_height = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSkirt_height(skirt_height);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSkirts method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSkirts()
//    {
//        System.out.println("getSkirts");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getSkirts();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSkirts method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSkirts()
//    {
//        System.out.println("setSkirts");
//        IntegerProperty skirts = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSkirts(skirts);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSolid_fill_pattern method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSolid_fill_pattern()
//    {
//        System.out.println("getSolid_fill_pattern");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getSolid_fill_pattern();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSolid_fill_pattern method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSolid_fill_pattern()
//    {
//        System.out.println("setSolid_fill_pattern");
//        StringProperty solid_fill_pattern = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSolid_fill_pattern(solid_fill_pattern);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getSolid_infill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetSolid_infill_extrusion_width()
//    {
//        System.out.println("getSolid_infill_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getSolid_infill_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSolid_infill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSolid_infill_extrusion_width()
//    {
//        System.out.println("setSolid_infill_extrusion_width");
//        float solid_infill_extrusion_width = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSolid_infill_extrusion_width(solid_infill_extrusion_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of spiral_vaseProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testSpiral_vaseProperty()
//    {
//        System.out.println("spiral_vaseProperty");
//        SlicerParameters instance = new SlicerParameters();
//        BooleanProperty expResult = null;
//        BooleanProperty result = instance.spiral_vaseProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setSpiral_vase method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetSpiral_vase()
//    {
//        System.out.println("setSpiral_vase");
//        boolean spiral_vase = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setSpiral_vase(spiral_vase);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getThreads method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetThreads()
//    {
//        System.out.println("getThreads");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.getThreads();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setThreads method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetThreads()
//    {
//        System.out.println("setThreads");
//        IntegerProperty threads = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setThreads(threads);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getTop_infill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetTop_infill_extrusion_width()
//    {
//        System.out.println("getTop_infill_extrusion_width");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getTop_infill_extrusion_width();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTop_infill_extrusion_width method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetTop_infill_extrusion_width()
//    {
//        System.out.println("setTop_infill_extrusion_width");
//        float top_infill_extrusion_width = 0.0F;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setTop_infill_extrusion_width(top_infill_extrusion_width);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of top_solid_layersProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testTop_solid_layersProperty()
//    {
//        System.out.println("top_solid_layersProperty");
//        SlicerParameters instance = new SlicerParameters();
//        IntegerProperty expResult = null;
//        IntegerProperty result = instance.top_solid_layersProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setTop_solid_layers method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetTop_solid_layers()
//    {
//        System.out.println("setTop_solid_layers");
//        int top_solid_layers = 0;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setTop_solid_layers(top_solid_layers);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getUn_retract_ratio method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetUn_retract_ratio()
//    {
//        System.out.println("getUn_retract_ratio");
//        SlicerParameters instance = new SlicerParameters();
//        FloatProperty expResult = null;
//        FloatProperty result = instance.getUn_retract_ratio();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setUn_retract_ratio method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetUn_retract_ratio()
//    {
//        System.out.println("setUn_retract_ratio");
//        FloatProperty un_retract_ratio = null;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setUn_retract_ratio(un_retract_ratio);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAutowipe method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetAutowipe()
//    {
//        System.out.println("getAutowipe");
//        SlicerParameters instance = new SlicerParameters();
//        boolean expResult = false;
//        boolean result = instance.getAutowipe();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isMutable method, of class SlicerParameters.
//     */
//    @Test
//    public void testIsMutable()
//    {
//        System.out.println("isMutable");
//        SlicerParameters instance = new SlicerParameters();
//        boolean expResult = false;
//        boolean result = instance.isMutable();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setMutable method, of class SlicerParameters.
//     */
//    @Test
//    public void testSetMutable()
//    {
//        System.out.println("setMutable");
//        boolean mutable = false;
//        SlicerParameters instance = new SlicerParameters();
//        instance.setMutable(mutable);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getProfileName method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetProfileName()
//    {
//        System.out.println("getProfileName");
//        SlicerParameters instance = new SlicerParameters();
//        String expResult = "";
//        String result = instance.getProfileName();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getProfileNameProperty method, of class SlicerParameters.
//     */
//    @Test
//    public void testGetProfileNameProperty()
//    {
//        System.out.println("getProfileNameProperty");
//        SlicerParameters instance = new SlicerParameters();
//        StringProperty expResult = null;
//        StringProperty result = instance.getProfileNameProperty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of readFromFile method, of class SlicerParameters.
//     */
//    @Test
//    public void testReadFromFile()
//    {
//        System.out.println("readFromFile");
//        String profileName = "";
//        boolean mutable = false;
//        String filename = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.readFromFile(profileName, mutable, filename);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of writeToFile method, of class SlicerParameters.
//     */
//    @Test
//    public void testWriteToFile()
//    {
//        System.out.println("writeToFile");
//        String filename = "";
//        SlicerParameters instance = new SlicerParameters();
//        instance.writeToFile(filename);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of clone method, of class SlicerParameters.
//     */
//    @Test
//    public void testClone()
//    {
//        System.out.println("clone");
//        SlicerParameters instance = new SlicerParameters();
//        SlicerParameters expResult = null;
//        SlicerParameters result = instance.clone();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toString method, of class SlicerParameters.
//     */
//    @Test
//    public void testToString()
//    {
//        System.out.println("toString");
//        SlicerParameters instance = new SlicerParameters();
//        String expResult = "";
//        String result = instance.toString();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
