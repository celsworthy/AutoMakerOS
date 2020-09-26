package celtech.legacy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class RoboxProfile implements Serializable, Cloneable
{
    private int LOCAL_version_number = 1;
    private Stenographer LOCAL_steno = StenographerFactory.getStenographer(RoboxProfile.class.getName());
    private StringProperty LOCAL_profileName = new SimpleStringProperty("");
    private boolean LOCAL_mutable = false;
    private NumberFormat LOCAL_numberFormatter = NumberFormat.getInstance(Locale.UK);

    //Immutable
    /**
     *
     */
    protected StringProperty print_center = new SimpleStringProperty("105,75");

    /**
     *
     */
    protected ObservableList<IntegerProperty> bed_size = FXCollections.observableArrayList(new SimpleIntegerProperty(226), new SimpleIntegerProperty(160));

    /**
     *
     */
    protected FloatProperty z_offset = new SimpleFloatProperty(0.0f);

    /**
     *
     */
    protected StringProperty gcode_flavor = new SimpleStringProperty("reprap");

    /**
     *
     */
    protected BooleanProperty use_relative_e_distances = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected IntegerProperty vibration_limit = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected StringProperty end_gcode = new SimpleStringProperty("");

    /**
     *
     */
    protected StringProperty layer_gcode = new SimpleStringProperty("");

    /**
     *
     */
    protected StringProperty toolchange_gcode = new SimpleStringProperty("");

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_before_travel = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_length = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_length_toolchange = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_lift = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_restart_extra = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> retract_restart_extra_toolchange = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<IntegerProperty> retract_speed = FXCollections.observableArrayList(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

    /**
     *
     */
    protected ObservableList<BooleanProperty> retract_layer_change = FXCollections.observableArrayList(new SimpleBooleanProperty(false), new SimpleBooleanProperty(false));

    /**
     *
     */
    protected ObservableList<IntegerProperty> wipe = FXCollections.observableArrayList(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_diameter = FXCollections.observableArrayList(new SimpleFloatProperty(0.3f), new SimpleFloatProperty(0.8f));

    /**
     *
     */
    protected BooleanProperty ooze_prevention = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty thin_walls = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty use_firmware_retraction = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected IntegerProperty perimeter_acceleration = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty infill_acceleration = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty bridge_acceleration = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty default_acceleration = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected BooleanProperty infill_only_where_needed = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected IntegerProperty solid_infill_every_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty fill_angle = new SimpleIntegerProperty(45);

    /**
     *
     */
    protected IntegerProperty solid_infill_below_area = new SimpleIntegerProperty(70);

    /**
     *
     */
    protected BooleanProperty only_retract_when_crossing_perimeters = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty infill_first = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty cooling = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected BooleanProperty fan_always_on = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected IntegerProperty max_fan_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty min_fan_speed = new SimpleIntegerProperty(100);

    /**
     *
     */
    protected IntegerProperty bridge_fan_speed = new SimpleIntegerProperty(100);

    /**
     *
     */
    protected IntegerProperty disable_fan_first_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty fan_below_layer_time = new SimpleIntegerProperty(60);

    /**
     *
     */
    protected IntegerProperty slowdown_below_layer_time = new SimpleIntegerProperty(15);

    /**
     *
     */
    protected IntegerProperty min_print_speed = new SimpleIntegerProperty(15);

    /**
     *
     */
    protected BooleanProperty avoid_crossing_perimeters = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected FloatProperty bridge_flow_ratio = new SimpleFloatProperty(1);

    /**
     *
     */
    protected IntegerProperty brim_width = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected BooleanProperty complete_objects = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty external_perimeters_first = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected BooleanProperty extra_perimeters = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected IntegerProperty extruder_clearance_height = new SimpleIntegerProperty(20);

    /**
     *
     */
    protected IntegerProperty extruder_clearance_radius = new SimpleIntegerProperty(20);

    /**
     *
     */
    protected StringProperty extrusion_axis = new SimpleStringProperty("E");

    /**
     *
     */
    protected FloatProperty first_layer_extrusion_width = new SimpleFloatProperty(0.5f);

    /**
     *
     */
    protected FloatProperty first_layer_height = new SimpleFloatProperty(0.2f);

    /**
     *
     */
    protected IntegerProperty g0 = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty gcode_arcs = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected BooleanProperty gcode_comments = new SimpleBooleanProperty(true);

    /**
     *
     */
    protected IntegerProperty infill_extruder = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty min_skirt_length = new SimpleIntegerProperty(5);

    /**
     *
     */
    protected StringProperty notes = new SimpleStringProperty("");

    /**
     *
     */
    protected StringProperty output_filename_format = new SimpleStringProperty("[input_filename_base].gcode");

    /**
     *
     */
    protected IntegerProperty perimeter_extruder = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected StringProperty post_process = new SimpleStringProperty("");

    /**
     *
     */
    protected BooleanProperty randomize_start = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected IntegerProperty resolution = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty rotate = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty scale = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty skirt_distance = new SimpleIntegerProperty(6);

    /**
     *
     */
    protected IntegerProperty skirt_height = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty skirts = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected StringProperty solid_fill_pattern = new SimpleStringProperty("rectilinear");

    /**
     *
     */
    protected IntegerProperty threads = new SimpleIntegerProperty(8);

    /**
     *
     */
    protected IntegerProperty support_material_interface_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty support_material_interface_spacing = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty raft_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty travel_speed = new SimpleIntegerProperty(400);

    /**
     *
     */
    protected FloatProperty filament_diameter = new SimpleFloatProperty(1.75f);

    //END of firmware overridden
    //Advanced controls
    /**
     *
     */
    protected StringProperty start_gcode = new SimpleStringProperty("");

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_partial_b_minimum = FXCollections.observableArrayList(new SimpleFloatProperty(0.5f), new SimpleFloatProperty(0.5f));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_preejection_volume = FXCollections.observableArrayList(new SimpleFloatProperty(0f), new SimpleFloatProperty(0f));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_ejection_volume = FXCollections.observableArrayList(new SimpleFloatProperty(0.5f), new SimpleFloatProperty(0.5f));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_wipe_volume = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected FloatProperty fill_density = new SimpleFloatProperty(0.4f);

    /**
     *
     */
    protected StringProperty fill_pattern = new SimpleStringProperty("rectilinear");

    /**
     *
     */
    protected IntegerProperty infill_every_layers = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty bottom_solid_layers = new SimpleIntegerProperty(3);

    /**
     *
     */
    protected IntegerProperty top_solid_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected BooleanProperty support_material = new SimpleBooleanProperty(false); // DONE

    /**
     *
     */
    protected IntegerProperty support_material_threshold = new SimpleIntegerProperty(48);

    /**
     *
     */
    protected IntegerProperty support_material_enforce_layers = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected StringProperty support_material_pattern = new SimpleStringProperty("rectilinear");

    /**
     *
     */
    protected FloatProperty support_material_spacing = new SimpleFloatProperty(2.5f);

    /**
     *
     */
    protected IntegerProperty support_material_angle = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected FloatProperty layer_height = new SimpleFloatProperty(0.2f); //DONE

    /**
     *
     */
    protected IntegerProperty perimeter_speed = new SimpleIntegerProperty(25);

    /**
     *
     */
    protected IntegerProperty small_perimeter_speed = new SimpleIntegerProperty(20);

    /**
     *
     */
    protected IntegerProperty external_perimeter_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty infill_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty solid_infill_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty top_solid_infill_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty support_material_speed = new SimpleIntegerProperty(30);

    /**
     *
     */
    protected IntegerProperty bridge_speed = new SimpleIntegerProperty(20);

    /**
     *
     */
    protected IntegerProperty gap_fill_speed = new SimpleIntegerProperty(20);

    /**
     *
     */
    protected IntegerProperty first_layer_speed = new SimpleIntegerProperty(18);

    /**
     *
     */
    protected BooleanProperty spiral_vase = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected FloatProperty extrusion_width = new SimpleFloatProperty(0.8f);

    /**
     *
     */
    protected FloatProperty perimeter_extrusion_width = new SimpleFloatProperty(0.3f);

    /**
     *
     */
    protected FloatProperty infill_extrusion_width = new SimpleFloatProperty(0.3f);

    /**
     *
     */
    protected FloatProperty solid_infill_extrusion_width = new SimpleFloatProperty(0.3f);

    /**
     *
     */
    protected FloatProperty top_infill_extrusion_width = new SimpleFloatProperty(0.3f);

    /**
     *
     */
    protected FloatProperty support_material_extrusion_width = new SimpleFloatProperty(0.3f);

    /**
     *
     */
    protected IntegerProperty perimeters = new SimpleIntegerProperty(3);

    /**
     *
     */
    protected BooleanProperty overhangs = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected IntegerProperty support_material_extruder = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty support_material_interface_extruder = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty first_layer_acceleration = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected BooleanProperty autowipe = new SimpleBooleanProperty(false);

    /*
     * Introduced in version 1.1.4 of Slic3r
     */
    /**
     *
     */
    protected BooleanProperty dont_support_bridges = new SimpleBooleanProperty(false);

    /**
     *
     */
    protected IntegerProperty interface_shells = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected StringProperty seam_position = new SimpleStringProperty("aligned");

    /**
     *
     */
    protected FloatProperty standby_temperature_delta = new SimpleFloatProperty(-5f);

    /**
     *
     */
    protected IntegerProperty support_material_interface_speed = new SimpleIntegerProperty(40);

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_close_at_midpoint = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_close_midpoint_percent = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected ObservableList<FloatProperty> nozzle_open_over_volume = FXCollections.observableArrayList(new SimpleFloatProperty(0), new SimpleFloatProperty(0));

    /**
     *
     */
    protected IntegerProperty force_nozzle_on_first_layer = new SimpleIntegerProperty(-1);

    /**
     *
     */
    protected IntegerProperty perimeter_nozzle = new SimpleIntegerProperty(0);

    /**
     *
     */
    protected IntegerProperty fill_nozzle = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty support_nozzle = new SimpleIntegerProperty(1);

    /**
     *
     */
    protected IntegerProperty support_interface_nozzle = new SimpleIntegerProperty(0);

    /**
     *
     */
    public RoboxProfile()
    {
        this.LOCAL_numberFormatter.setMaximumFractionDigits(2);
    }

    /**
     *
     * @param mutable
     */
    public RoboxProfile(boolean mutable)
    {
        this.LOCAL_mutable = mutable;
        this.LOCAL_numberFormatter.setMaximumFractionDigits(2);
    }

    /**
     *
     * @param value
     */
    public void setFilament_diameter(float value)
    {
        filament_diameter.set(value);
    }

    /**
     *
     * @return
     */
    public float getFilament_diameter()
    {
        return filament_diameter.get();
    }

    /**
     *
     * @return
     */
    public FloatProperty filament_diameterProperty()
    {
        return filament_diameter;
    }

    //END Advanced controls
    //Common options
    //END Common options
    //Other stuff
    /**
     *
     * @return
     */
    public StringProperty getPrint_center()
    {
        return print_center;
    }

    /**
     *
     * @param print_center
     */
    public void setPrint_center(StringProperty print_center)
    {
        this.print_center = print_center;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getRetract_restart_extra_toolchange()
    {
        return retract_restart_extra_toolchange;
    }

    /**
     *
     * @param retract_restart_extra_toolchange
     */
    public void setRetract_restart_extra_toolchange(ObservableList<FloatProperty> retract_restart_extra_toolchange)
    {
        this.retract_restart_extra_toolchange = retract_restart_extra_toolchange;
    }

    /**
     *
     * @return
     */
    public ObservableList<IntegerProperty> getBed_size()
    {
        return bed_size;
    }

    /**
     *
     * @param bed_size
     */
    public void setBed_size(ObservableList<IntegerProperty> bed_size)
    {
        this.bed_size = bed_size;
    }

    /**
     *
     * @return
     */
    public FloatProperty getZ_offset()
    {
        return z_offset;
    }

    /**
     *
     * @param z_offset
     */
    public void setZ_offset(FloatProperty z_offset)
    {
        this.z_offset = z_offset;
    }

    /**
     *
     * @return
     */
    public StringProperty getGcode_flavor()
    {
        return gcode_flavor;
    }

    /**
     *
     * @param gcode_flavor
     */
    public void setGcode_flavor(StringProperty gcode_flavor)
    {
        this.gcode_flavor = gcode_flavor;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getUse_relative_e_distances()
    {
        return use_relative_e_distances;
    }

    /**
     *
     * @param use_relative_e_distances
     */
    public void setUse_relative_e_distances(boolean use_relative_e_distances)
    {
        this.use_relative_e_distances.set(use_relative_e_distances);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getVibration_limit()
    {
        return vibration_limit;
    }

    /**
     *
     * @param vibration_limit
     */
    public void setVibration_limit(IntegerProperty vibration_limit)
    {
        this.vibration_limit = vibration_limit;
    }

    /**
     *
     * @return
     */
    public StringProperty getStart_gcode()
    {
        return start_gcode;
    }

    /**
     *
     * @param start_gcode
     */
    public void setStart_gcode(String start_gcode)
    {
        this.start_gcode.set(start_gcode);
    }

    /**
     *
     * @return
     */
    public StringProperty getEnd_gcode()
    {
        return end_gcode;
    }

    /**
     *
     * @param end_gcode
     */
    public void setEnd_gcode(String end_gcode)
    {
        this.end_gcode.set(end_gcode);
    }

    /**
     *
     * @return
     */
    public StringProperty getLayer_gcode()
    {
        return layer_gcode;
    }

    /**
     *
     * @param layer_gcode
     */
    public void setLayer_gcode(StringProperty layer_gcode)
    {
        this.layer_gcode = layer_gcode;
    }

    /**
     *
     * @return
     */
    public StringProperty getToolchange_gcode()
    {
        return toolchange_gcode;
    }

    /**
     *
     * @param toolchange_gcode
     */
    public void setToolchange_gcode(StringProperty toolchange_gcode)
    {
        this.toolchange_gcode = toolchange_gcode;
    }

    /**
     *
     * @return
     */
    public IntegerProperty perimeter_extruderProperty()
    {
        return perimeter_extruder;
    }

    /**
     *
     * @param perimeter_extruder
     */
    public void setPerimeter_extruder(int perimeter_extruder)
    {
        this.perimeter_extruder.set(perimeter_extruder);
    }

    /**
     *
     * @return
     */
    public IntegerProperty infill_extruderProperty()
    {
        return infill_extruder;
    }

    /**
     *
     * @param infill_nozzle
     */
    public void setInfill_extruder(int infill_nozzle)
    {
        this.infill_extruder.set(infill_nozzle);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_extruderProperty()
    {
        return support_material_extruder;
    }

    /**
     *
     * @param support_material_extruder
     */
    public void setSupport_material_extruder(int support_material_extruder)
    {
        this.support_material_extruder.set(support_material_extruder);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_interface_extruderProperty()
    {
        return support_material_interface_extruder;
    }

    /**
     *
     * @param support_material_interface_extruder
     */
    public void setSupport_material_interface_extruder(int support_material_interface_extruder)
    {
        this.support_material_interface_extruder.set(support_material_interface_extruder);
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getRetract_lift()
    {
        return retract_lift;
    }

    /**
     *
     * @param retract_lift
     */
    public void setRetract_lift(ObservableList<FloatProperty> retract_lift)
    {
        this.retract_lift = retract_lift;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getRetract_restart_extra()
    {
        return retract_restart_extra;
    }

    /**
     *
     * @param retract_restart_extra
     */
    public void setRetract_restart_extra(ObservableList<FloatProperty> retract_restart_extra)
    {
        this.retract_restart_extra = retract_restart_extra;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getRetract_before_travel()
    {
        return retract_before_travel;
    }

    /**
     *
     * @param retract_before_travel
     */
    public void setRetract_before_travel(ObservableList<FloatProperty> retract_before_travel)
    {
        this.retract_before_travel = retract_before_travel;
    }

    /**
     *
     * @return
     */
    public ObservableList<BooleanProperty> getRetract_layer_change()
    {
        return retract_layer_change;
    }

    /**
     *
     * @param retract_layer_change
     */
    public void setRetract_layer_change(ObservableList<BooleanProperty> retract_layer_change)
    {
        this.retract_layer_change = retract_layer_change;
    }

    /**
     *
     * @return
     */
    public ObservableList<IntegerProperty> getWipe()
    {
        return wipe;
    }

    /**
     *
     * @param wipe
     */
    public void setWipe(ObservableList<IntegerProperty> wipe)
    {
        this.wipe = wipe;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_diameter()
    {
        return nozzle_diameter;
    }

    /**
     *
     * @param nozzle_diameter
     */
    public void setNozzle_diameter(ObservableList<FloatProperty> nozzle_diameter)
    {
        this.nozzle_diameter = nozzle_diameter;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getPerimeter_acceleration()
    {
        return perimeter_acceleration;
    }

    /**
     *
     * @param perimeter_acceleration
     */
    public void setPerimeter_acceleration(IntegerProperty perimeter_acceleration)
    {
        this.perimeter_acceleration = perimeter_acceleration;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getInfill_acceleration()
    {
        return infill_acceleration;
    }

    /**
     *
     * @param infill_acceleration
     */
    public void setInfill_acceleration(IntegerProperty infill_acceleration)
    {
        this.infill_acceleration = infill_acceleration;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getBridge_acceleration()
    {
        return bridge_acceleration;
    }

    /**
     *
     * @param bridge_acceleration
     */
    public void setBridge_acceleration(IntegerProperty bridge_acceleration)
    {
        this.bridge_acceleration = bridge_acceleration;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getDefault_acceleration()
    {
        return default_acceleration;
    }

    /**
     *
     * @param default_acceleration
     */
    public void setDefault_acceleration(IntegerProperty default_acceleration)
    {
        this.default_acceleration = default_acceleration;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> retract_lengthProperty()
    {
        return retract_length;
    }

    /**
     *
     * @param retract_length
     */
    public void setRetract_length(ObservableList<FloatProperty> retract_length)
    {
        this.retract_length = retract_length;
    }

    /**
     *
     * @return
     */
    public ObservableList<IntegerProperty> retract_speedProperty()
    {
        return retract_speed;
    }

    /**
     *
     * @param retract_speed
     */
    public void setRetract_speed(ObservableList<IntegerProperty> retract_speed)
    {
        this.retract_speed = retract_speed;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_ejection_volume()
    {
        return nozzle_ejection_volume;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_preejection_volume()
    {
        return nozzle_preejection_volume;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_partial_b_minimum()
    {
        return nozzle_partial_b_minimum;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_wipe_volume()
    {
        return nozzle_wipe_volume;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getFan_always_on()
    {
        return fan_always_on;
    }

    /**
     *
     * @param fan_always_on
     */
    public void setFan_always_on(BooleanProperty fan_always_on)
    {
        this.fan_always_on = fan_always_on;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getCooling()
    {
        return cooling;
    }

    /**
     *
     * @param cooling
     */
    public void setCooling(BooleanProperty cooling)
    {
        this.cooling = cooling;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getMax_fan_speed()
    {
        return max_fan_speed;
    }

    /**
     *
     * @param max_fan_speed
     */
    public void setMax_fan_speed(IntegerProperty max_fan_speed)
    {
        this.max_fan_speed = max_fan_speed;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getMin_fan_speed()
    {
        return min_fan_speed;
    }

    /**
     *
     * @param min_fan_speed
     */
    public void setMin_fan_speed(IntegerProperty min_fan_speed)
    {
        this.min_fan_speed = min_fan_speed;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getBridge_fan_speed()
    {
        return bridge_fan_speed;
    }

    /**
     *
     * @param bridge_fan_speed
     */
    public void setBridge_fan_speed(IntegerProperty bridge_fan_speed)
    {
        this.bridge_fan_speed = bridge_fan_speed;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getDisable_fan_first_layers()
    {
        return disable_fan_first_layers;
    }

    /**
     *
     * @param disable_fan_first_layers
     */
    public void setDisable_fan_first_layers(IntegerProperty disable_fan_first_layers)
    {
        this.disable_fan_first_layers = disable_fan_first_layers;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getFan_below_layer_time()
    {
        return fan_below_layer_time;
    }

    /**
     *
     * @param fan_below_layer_time
     */
    public void setFan_below_layer_time(IntegerProperty fan_below_layer_time)
    {
        this.fan_below_layer_time = fan_below_layer_time;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSlowdown_below_layer_time()
    {
        return slowdown_below_layer_time;
    }

    /**
     *
     * @param slowdown_below_layer_time
     */
    public void setSlowdown_below_layer_time(IntegerProperty slowdown_below_layer_time)
    {
        this.slowdown_below_layer_time = slowdown_below_layer_time;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getMin_print_speed()
    {
        return min_print_speed;
    }

    /**
     *
     * @param min_print_speed
     */
    public void setMin_print_speed(IntegerProperty min_print_speed)
    {
        this.min_print_speed = min_print_speed;
    }

    /**
     *
     * @return
     */
    public FloatProperty fill_densityProperty()
    {
        return fill_density;
    }

    /**
     *
     * @param fill_density
     */
    public void setFill_density(float fill_density)
    {
        this.fill_density.set(fill_density);
    }

    /**
     *
     * @return
     */
    public StringProperty fill_patternProperty()
    {
        return fill_pattern;
    }

    /**
     *
     * @param fill_pattern
     */
    public void setFill_pattern(String fill_pattern)
    {
        this.fill_pattern.set(fill_pattern);
    }

    /**
     *
     * @return
     */
    public IntegerProperty infill_every_layersProperty()
    {
        return infill_every_layers;
    }

    /**
     *
     * @param infill_every_layers
     */
    public void setInfill_every_layers(int infill_every_layers)
    {
        this.infill_every_layers.set(infill_every_layers);
    }

    /**
     *
     * @return
     */
    public BooleanProperty getInfill_only_where_needed()
    {
        return infill_only_where_needed;
    }

    /**
     *
     * @param infill_only_where_needed
     */
    public void setInfill_only_where_needed(boolean infill_only_where_needed)
    {
        this.infill_only_where_needed.set(infill_only_where_needed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSolid_infill_every_layers()
    {
        return solid_infill_every_layers;
    }

    /**
     *
     * @param solid_infill_every_layers
     */
    public void setSolid_infill_every_layers(int solid_infill_every_layers)
    {
        this.solid_infill_every_layers.set(solid_infill_every_layers);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getFill_angle()
    {
        return fill_angle;
    }

    /**
     *
     * @param fill_angle
     */
    public void setFill_angle(IntegerProperty fill_angle)
    {
        this.fill_angle = fill_angle;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSolid_infill_below_area()
    {
        return solid_infill_below_area;
    }

    /**
     *
     * @param solid_infill_below_area
     */
    public void setSolid_infill_below_area(IntegerProperty solid_infill_below_area)
    {
        this.solid_infill_below_area = solid_infill_below_area;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getOnly_retract_when_crossing_perimeters()
    {
        return only_retract_when_crossing_perimeters;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getInfill_first()
    {
        return infill_first;
    }

    /**
     *
     * @return
     */
    public IntegerProperty perimeter_speedProperty()
    {
        return perimeter_speed;
    }

    /**
     *
     * @param perimeter_speed
     */
    public void setPerimeter_speed(int perimeter_speed)
    {
        this.perimeter_speed.set(perimeter_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty small_perimeter_speedProperty()
    {
        return small_perimeter_speed;
    }

    /**
     *
     * @param small_perimeter_speed
     */
    public void setSmall_perimeter_speed(int small_perimeter_speed)
    {
        this.small_perimeter_speed.set(small_perimeter_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty external_perimeter_speedProperty()
    {
        return external_perimeter_speed;
    }

    /**
     *
     * @param external_perimeter_speed
     */
    public void setExternal_perimeter_speed(int external_perimeter_speed)
    {
        this.external_perimeter_speed.set(external_perimeter_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty infill_speedProperty()
    {
        return infill_speed;
    }

    /**
     *
     * @param infill_speed
     */
    public void setInfill_speed(int infill_speed)
    {
        this.infill_speed.set(infill_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty solid_infill_speedProperty()
    {
        return solid_infill_speed;
    }

    /**
     *
     * @param solid_infill_speed
     */
    public void setSolid_infill_speed(int solid_infill_speed)
    {
        this.solid_infill_speed.set(solid_infill_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty top_solid_infill_speedProperty()
    {
        return top_solid_infill_speed;
    }

    /**
     *
     * @param top_solid_infill_speed
     */
    public void setTop_solid_infill_speed(int top_solid_infill_speed)
    {
        this.top_solid_infill_speed.set(top_solid_infill_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_speedProperty()
    {
        return support_material_speed;
    }

    /**
     *
     * @param support_material_speed
     */
    public void setSupport_material_speed(int support_material_speed)
    {
        this.support_material_speed.set(support_material_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty bridge_speedProperty()
    {
        return bridge_speed;
    }

    /**
     *
     * @param bridge_speed
     */
    public void setBridge_speed(int bridge_speed)
    {
        this.bridge_speed.set(bridge_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty gap_fill_speedProperty()
    {
        return gap_fill_speed;
    }

    /**
     *
     * @param gap_fill_speed
     */
    public void setGap_fill_speed(int gap_fill_speed)
    {
        this.gap_fill_speed.set(gap_fill_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty travel_speedProperty()
    {
        return travel_speed;
    }

    /**
     *
     * @param travel_speed
     */
    public void setTravel_speed(int travel_speed)
    {
        this.travel_speed.set(travel_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty first_layer_speedProperty()
    {
        return first_layer_speed;
    }

    /**
     *
     * @param first_layer_speed
     */
    public void setFirst_layer_speed(int first_layer_speed)
    {
        this.first_layer_speed.set(first_layer_speed);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_thresholdProperty()
    {
        return support_material_threshold;
    }

    /**
     *
     * @param support_material_threshold
     */
    public void setSupport_material_threshold(int support_material_threshold)
    {
        this.support_material_threshold.set(support_material_threshold);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_enforce_layersProperty()
    {
        return support_material_enforce_layers;
    }

    /**
     *
     * @param support_material_enforce_layers
     */
    public void setSupport_material_enforce_layers(int support_material_enforce_layers)
    {
        this.support_material_enforce_layers.set(support_material_enforce_layers);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getRaft_layers()
    {
        return raft_layers;
    }

    /**
     *
     * @param raft_layers
     */
    public void setRaft_layers(IntegerProperty raft_layers)
    {
        this.raft_layers = raft_layers;
    }

    /**
     *
     * @return
     */
    public StringProperty support_material_patternProperty()
    {
        return support_material_pattern;
    }

    /**
     *
     * @param support_material_pattern
     */
    public void setSupport_material_pattern(String support_material_pattern)
    {
        this.support_material_pattern.set(support_material_pattern);
    }

    /**
     *
     * @return
     */
    public FloatProperty support_material_spacingProperty()
    {
        return support_material_spacing;
    }

    /**
     *
     * @param support_material_spacing
     */
    public void setSupport_material_spacing(float support_material_spacing)
    {
        this.support_material_spacing.set(support_material_spacing);
    }

    /**
     *
     * @return
     */
    public IntegerProperty support_material_angleProperty()
    {
        return support_material_angle;
    }

    /**
     *
     * @param support_material_angle
     */
    public void setSupport_material_angle(int support_material_angle)
    {
        this.support_material_angle.set(support_material_angle);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSupport_material_interface_layers()
    {
        return support_material_interface_layers;
    }

    /**
     *
     * @param support_material_interface_layers
     */
    public void setSupport_material_interface_layers(IntegerProperty support_material_interface_layers)
    {
        this.support_material_interface_layers = support_material_interface_layers;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSupport_material_interface_spacing()
    {
        return support_material_interface_spacing;
    }

    /**
     *
     * @param support_material_interface_spacing
     */
    public void setSupport_material_interface_spacing(IntegerProperty support_material_interface_spacing)
    {
        this.support_material_interface_spacing = support_material_interface_spacing;
    }

    /**
     *
     * @return
     */
    public FloatProperty getLayer_height()
    {
        return layer_height;
    }

    /**
     *
     * @param layer_height
     */
    public void setLayer_height(FloatProperty layer_height)
    {
        this.layer_height = layer_height;
    }

    /**
     *
     * @return
     */
    public BooleanProperty support_materialProperty()
    {
        return support_material;
    }

    /**
     *
     * @param support_material
     */
    public void setSupport_material(boolean support_material)
    {
        this.support_material.set(support_material);
    }

    /**
     *
     * @return
     */
    public BooleanProperty getAvoid_crossing_perimeters()
    {
        return avoid_crossing_perimeters;
    }

    /**
     *
     * @param avoid_crossing_perimeters
     */
    public void setAvoid_crossing_perimeters(boolean avoid_crossing_perimeters)
    {
        this.avoid_crossing_perimeters.set(avoid_crossing_perimeters);
    }

    /**
     *
     * @return
     */
    public IntegerProperty bottom_solid_layersProperty()
    {
        return bottom_solid_layers;
    }

    /**
     *
     * @param bottom_solid_layers
     */
    public void setBottom_solid_layers(int bottom_solid_layers)
    {
        this.bottom_solid_layers.set(bottom_solid_layers);
    }

    /**
     *
     * @return
     */
    public FloatProperty getBridge_flow_ratio()
    {
        return bridge_flow_ratio;
    }

    /**
     *
     * @param bridge_flow_ratio
     */
    public void setBridge_flow_ratio(FloatProperty bridge_flow_ratio)
    {
        this.bridge_flow_ratio = bridge_flow_ratio;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getBrim_width()
    {
        return brim_width;
    }

    /**
     *
     * @param brim_width
     */
    public void setBrim_width(IntegerProperty brim_width)
    {
        this.brim_width = brim_width;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getComplete_objects()
    {
        return complete_objects;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getExternal_perimeters_first()
    {
        return external_perimeters_first;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getExtra_perimeters()
    {
        return extra_perimeters;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getExtruder_clearance_height()
    {
        return extruder_clearance_height;
    }

    /**
     *
     * @param extruder_clearance_height
     */
    public void setExtruder_clearance_height(IntegerProperty extruder_clearance_height)
    {
        this.extruder_clearance_height = extruder_clearance_height;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getExtruder_clearance_radius()
    {
        return extruder_clearance_radius;
    }

    /**
     *
     * @param extruder_clearance_radius
     */
    public void setExtruder_clearance_radius(IntegerProperty extruder_clearance_radius)
    {
        this.extruder_clearance_radius = extruder_clearance_radius;
    }

    /**
     *
     * @return
     */
    public StringProperty getExtrusion_axis()
    {
        return extrusion_axis;
    }

    /**
     *
     * @param extrusion_axis
     */
    public void setExtrusion_axis(StringProperty extrusion_axis)
    {
        this.extrusion_axis = extrusion_axis;
    }

    /**
     *
     * @return
     */
    public FloatProperty getExtrusion_width()
    {
        return extrusion_width;
    }

    /**
     *
     * @return
     */
    public FloatProperty getSupport_material_extrusion_width()
    {
        return support_material_extrusion_width;
    }

    /**
     *
     * @return
     */
    public FloatProperty getFirst_layer_extrusion_width()
    {
        return first_layer_extrusion_width;
    }

    /**
     *
     * @return
     */
    public FloatProperty getFirst_layer_height()
    {
        return first_layer_height;
    }

    /**
     *
     * @param first_layer_height
     */
    public void setFirst_layer_height(FloatProperty first_layer_height)
    {
        this.first_layer_height = first_layer_height;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getG0()
    {
        return g0;
    }

    /**
     *
     * @param g0
     */
    public void setG0(IntegerProperty g0)
    {
        this.g0 = g0;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getGcode_arcs()
    {
        return gcode_arcs;
    }

    /**
     *
     * @param gcode_arcs
     */
    public void setGcode_arcs(IntegerProperty gcode_arcs)
    {
        this.gcode_arcs = gcode_arcs;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getGcode_comments()
    {
        return gcode_comments;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getInfill_extruder()
    {
        return infill_extruder;
    }

    /**
     *
     * @param infill_extruder
     */
    public void setInfill_extruder(IntegerProperty infill_extruder)
    {
        this.infill_extruder = infill_extruder;
    }

    /**
     *
     * @return
     */
    public FloatProperty getInfill_extrusion_width()
    {
        return infill_extrusion_width;
    }

    /**
     *
     * @param infill_extrusion_width
     */
    public void setInfill_extrusion_width(float infill_extrusion_width)
    {
        this.infill_extrusion_width.set(infill_extrusion_width);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getMin_skirt_length()
    {
        return min_skirt_length;
    }

    /**
     *
     * @param min_skirt_length
     */
    public void setMin_skirt_length(IntegerProperty min_skirt_length)
    {
        this.min_skirt_length = min_skirt_length;
    }

    /**
     *
     * @return
     */
    public StringProperty getNotes()
    {
        return notes;
    }

    /**
     *
     * @param notes
     */
    public void setNotes(StringProperty notes)
    {
        this.notes = notes;
    }

    /**
     *
     * @return
     */
    public StringProperty getOutput_filename_format()
    {
        return output_filename_format;
    }

    /**
     *
     * @param output_filename_format
     */
    public void setOutput_filename_format(StringProperty output_filename_format)
    {
        this.output_filename_format = output_filename_format;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getPerimeter_extruder()
    {
        return perimeter_extruder;
    }

    /**
     *
     * @param perimeter_extruder
     */
    public void setPerimeter_extruder(IntegerProperty perimeter_extruder)
    {
        this.perimeter_extruder = perimeter_extruder;
    }

    /**
     *
     * @return
     */
    public FloatProperty getPerimeter_extrusion_width()
    {
        return perimeter_extrusion_width;
    }

    /**
     *
     * @param perimeter_extrusion_width
     */
    public void setPerimeter_extrusion_width(float perimeter_extrusion_width)
    {
        this.perimeter_extrusion_width.set(perimeter_extrusion_width);
    }

    /**
     *
     * @return
     */
    public IntegerProperty perimetersProperty()
    {
        return perimeters;
    }

    /**
     *
     * @param perimeters
     */
    public void setPerimeters(int perimeters)
    {
        this.perimeters.set(perimeters);
    }

    /**
     *
     * @return
     */
    public StringProperty getPost_process()
    {
        return post_process;
    }

    /**
     *
     * @param post_process
     */
    public void setPost_process(StringProperty post_process)
    {
        this.post_process = post_process;
    }

    /**
     *
     * @return
     */
    public BooleanProperty getRandomize_start()
    {
        return randomize_start;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getResolution()
    {
        return resolution;
    }

    /**
     *
     * @param resolution
     */
    public void setResolution(IntegerProperty resolution)
    {
        this.resolution = resolution;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getRetract_length_toolchange()
    {
        return retract_length_toolchange;
    }

    /**
     *
     * @param retract_length_toolchange
     */
    public void setRetract_length_toolchange(ObservableList<FloatProperty> retract_length_toolchange)
    {
        this.retract_length_toolchange = retract_length_toolchange;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getRotate()
    {
        return rotate;
    }

    /**
     *
     * @param rotate
     */
    public void setRotate(IntegerProperty rotate)
    {
        this.rotate = rotate;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getScale()
    {
        return scale;
    }

    /**
     *
     * @param scale
     */
    public void setScale(IntegerProperty scale)
    {
        this.scale = scale;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSkirt_distance()
    {
        return skirt_distance;
    }

    /**
     *
     * @param skirt_distance
     */
    public void setSkirt_distance(IntegerProperty skirt_distance)
    {
        this.skirt_distance = skirt_distance;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSkirt_height()
    {
        return skirt_height;
    }

    /**
     *
     * @param skirt_height
     */
    public void setSkirt_height(IntegerProperty skirt_height)
    {
        this.skirt_height = skirt_height;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSkirts()
    {
        return skirts;
    }

    /**
     *
     * @param skirts
     */
    public void setSkirts(IntegerProperty skirts)
    {
        this.skirts = skirts;
    }

    /**
     *
     * @return
     */
    public StringProperty getSolid_fill_pattern()
    {
        return solid_fill_pattern;
    }

    /**
     *
     * @param solid_fill_pattern
     */
    public void setSolid_fill_pattern(StringProperty solid_fill_pattern)
    {
        this.solid_fill_pattern = solid_fill_pattern;
    }

    /**
     *
     * @return
     */
    public FloatProperty getSolid_infill_extrusion_width()
    {
        return solid_infill_extrusion_width;
    }

    /**
     *
     * @param solid_infill_extrusion_width
     */
    public void setSolid_infill_extrusion_width(float solid_infill_extrusion_width)
    {
        this.solid_infill_extrusion_width.set(solid_infill_extrusion_width);
    }

    /**
     *
     * @return
     */
    public BooleanProperty spiral_vaseProperty()
    {
        return spiral_vase;
    }

    /**
     *
     * @param spiral_vase
     */
    public void setSpiral_vase(boolean spiral_vase)
    {
        this.spiral_vase.set(spiral_vase);
    }

    /**
     *
     * @return
     */
    public IntegerProperty getThreads()
    {
        return threads;
    }

    /**
     *
     * @param threads
     */
    public void setThreads(IntegerProperty threads)
    {
        this.threads = threads;
    }

    /**
     *
     * @return
     */
    public FloatProperty getTop_infill_extrusion_width()
    {
        return top_infill_extrusion_width;
    }

    /**
     *
     * @param top_infill_extrusion_width
     */
    public void setTop_infill_extrusion_width(float top_infill_extrusion_width)
    {
        this.top_infill_extrusion_width.set(top_infill_extrusion_width);
    }

    /**
     *
     * @return
     */
    public IntegerProperty top_solid_layersProperty()
    {
        return top_solid_layers;
    }

    /**
     *
     * @param top_solid_layers
     */
    public void setTop_solid_layers(int top_solid_layers)
    {
        this.top_solid_layers.set(top_solid_layers);
    }

    /**
     *
     * @return
     */
    public boolean getAutowipe()
    {
        return autowipe.get();
    }

    /**
     *
     * @return
     */
    public boolean isMutable()
    {
        return LOCAL_mutable;
    }

    /**
     *
     * @param mutable
     */
    public void setMutable(boolean mutable)
    {
        LOCAL_mutable = mutable;
    }

    /**
     *
     * @return
     */
    public String getProfileName()
    {
        return LOCAL_profileName.get();
    }

    /**
     *
     * @return
     */
    public StringProperty getProfileNameProperty()
    {
        return LOCAL_profileName;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_close_at_midpoint()
    {
        return nozzle_close_at_midpoint;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_close_midpoint_percent()
    {
        return nozzle_close_midpoint_percent;
    }

    /**
     *
     * @return
     */
    public ObservableList<FloatProperty> getNozzle_open_over_volume()
    {
        return nozzle_open_over_volume;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getForce_nozzle_on_first_layer()
    {
        return force_nozzle_on_first_layer;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getPerimeterNozzleProperty()
    {
        return perimeter_nozzle;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSupportNozzleProperty()
    {
        return support_nozzle;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getSupportInterfaceNozzleProperty()
    {
        return support_interface_nozzle;
    }

    /**
     *
     * @return
     */
    public IntegerProperty getFillNozzleProperty()
    {
        return fill_nozzle;
    }

    /**
     *
     * @param profileName
     * @param mutable
     * @param filename
     */
    public void readFromFile(String profileName, boolean mutable, String filename)
    {
        LOCAL_version_number = -1;
        LOCAL_profileName.set(profileName);
        LOCAL_mutable = mutable;
        File inputFile = new File(filename);
        BufferedReader fileReader = null;

        try
        {
            Method setBooleanProperty = BooleanProperty.class.getDeclaredMethod("setValue", Boolean.class);
            Method setStringProperty = StringProperty.class.getDeclaredMethod("setValue", String.class);
            Method setIntegerProperty = IntegerProperty.class.getDeclaredMethod("setValue", Number.class);
            Method setFloatProperty = FloatProperty.class.getDeclaredMethod("setValue", Number.class);

            Method observableListGet = List.class.getDeclaredMethod("get", int.class);

            fileReader = new BufferedReader(new FileReader(inputFile));

            String lineToProcess = null;

            while ((lineToProcess = fileReader.readLine()) != null)
            {
                if (lineToProcess.equals("") == false)
                {
                    String[] lineParts = lineToProcess.trim().split("[ ]*=[ ]*");
                    try
                    {
                        Field field = this.getClass().getDeclaredField(lineParts[0]);
                        Class<?> fieldClass = field.getType();

                        if (fieldClass.equals(boolean.class))
                        {
                            boolean value = false;

                            if (lineParts.length == 2 && lineParts[1].equalsIgnoreCase("1"))
                            {
                                value = true;
                            }

                            field.setBoolean(this, value);
                        } else if (fieldClass.equals(BooleanProperty.class))
                        {
                            boolean value = false;

                            if (lineParts.length == 2 && lineParts[1].equalsIgnoreCase("1"))
                            {
                                value = true;
                            }
                            setBooleanProperty.invoke(field.get(this), value);
                        } else if (fieldClass.equals(StringProperty.class))
                        {
                            String value = "";
                            if (lineParts.length == 2)
                            {
                                value = lineParts[1];
                            }
                            setStringProperty.invoke(field.get(this), value);
                        } else if (fieldClass.equals(IntegerProperty.class))
                        {
                            int value = 0;
                            if (lineParts.length == 2)
                            {
                                value = Integer.valueOf(lineParts[1]);
                            } else
                            {
                                LOCAL_steno.warning("Field " + lineParts[0] + " is missing a value");
                            }
                            setIntegerProperty.invoke(field.get(this), value);
                        } else if (fieldClass.equals(FloatProperty.class))
                        {
                            float value = 0;
                            if (lineParts.length == 2)
                            {
                                value = Float.valueOf(lineParts[1]);
                            } else
                            {
                                LOCAL_steno.warning("Field " + lineParts[0] + " is missing a value");
                            }
                            setFloatProperty.invoke(field.get(this), value);
                        } else if (fieldClass.equals(ObservableList.class))
                        {
                            Type genericType = field.getGenericType();
                            Class<?> fieldContentClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                            String[] elements = lineParts[1].split(",");
                            int elementCounter = 0;

                            for (String element : elements)
                            {
                                if (fieldContentClass.equals(IntegerProperty.class))
                                {
                                    IntegerProperty property = (IntegerProperty) observableListGet.invoke(field.get(this), elementCounter);
                                    property.set(Integer.valueOf(element));
                                } else if (fieldContentClass.equals(FloatProperty.class))
                                {
                                    FloatProperty property = (FloatProperty) observableListGet.invoke(field.get(this), elementCounter);
                                    property.set(Float.valueOf(element));
                                } else if (fieldContentClass.equals(StringProperty.class))
                                {
                                    StringProperty property = (StringProperty) observableListGet.invoke(field.get(this), elementCounter);
                                    property.set(element);
                                } else if (fieldContentClass.equals(BooleanProperty.class))
                                {
                                    BooleanProperty property = (BooleanProperty) observableListGet.invoke(field.get(this), elementCounter);

                                    if (element.equalsIgnoreCase("1"))
                                    {
                                        property.set(true);
                                    } else
                                    {
                                        property.set(false);
                                    }
                                }

                                elementCounter++;
                            }
                        } else
                        {
                            LOCAL_steno.error("Couldn't process field " + lineParts[0]);
                        }
                    } catch (NoSuchFieldException ex)
                    {
                        if (lineParts[0].trim().startsWith("#") == false)
                        {
                            LOCAL_steno.error("Couldn't parse settings for field " + lineParts[0] + " " + ex);
                        }
                        else
                        {
                            // Special case for intercepting commented fields
                            // The version number will be stored like this
                            if (lineParts[0].trim().contains("Version"))
                            {
                                String versionField = lineParts[1];
                                if (versionField != null)
                                {
                                    LOCAL_version_number = Integer.valueOf(versionField);
                                }
                            }
                        }
                    } catch (IllegalAccessException ex)
                    {
                        LOCAL_steno.error("Access exception whilst setting " + lineParts[0] + " " + ex);
                    } catch (IllegalArgumentException ex)
                    {
                        LOCAL_steno.error("Illegal argument exception whilst setting " + lineParts[0] + " " + ex);
                    } catch (SecurityException ex)
                    {
                        LOCAL_steno.error("Security exception whilst setting " + lineParts[0] + " " + ex);
                    } catch (InvocationTargetException ex)
                    {
                        LOCAL_steno.error("Couldn't set up field " + lineParts[0] + " " + ex);
                    } catch (IndexOutOfBoundsException ex)
                    {
                        LOCAL_steno.error("Index out of bounds  " + lineParts[0] + " " + ex);
                    }
                }
            }

            fileReader.close();
        } catch (IOException ex)
        {
            LOCAL_steno.error("IO Exception when reading settings file " + filename);
        } catch (NoSuchMethodException ex)
        {
            LOCAL_steno.error("Couldn't establish reflection methods when reading settings file " + filename + " " + ex);
        }
    }

    /**
     *
     * @param filename
     */
    public void writeToFile(String filename)
    {
        File outputFile = new File(filename);
        FileWriter fileWriter = null;

        try
        {
            fileWriter = new FileWriter(outputFile);

            fileWriter.append("#Profile: " + LOCAL_profileName.get() + "\n");
            fileWriter.append("#Version = " + LOCAL_version_number + "\n");

            Field[] fields = this.getClass().getDeclaredFields();

            if (fields.length == 0)
            {
                fields = this.getClass().getSuperclass().getDeclaredFields();
            }

            for (Field field : fields)
            {
                try
                {
                    Class<?> fieldClass = field.getType();

                    if (field.getName().startsWith("LOCAL") == false)
                    {

                        if (fieldClass.isArray())
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append(field.getName());
                            sb.append(" = ");
                            Object fieldValue = field.get(this);

                            int length = Array.getLength(fieldValue);
                            for (int i = 0; i < length; i++)
                            {
                                Object arrayElement = Array.get(fieldValue, i);
                                sb.append(arrayElement);
                                if (i < (length - 1))
                                {
                                    sb.append(",");
                                }
                            }
                            sb.append("\n");
                            fileWriter.write(sb.toString());

                        } else if (fieldClass.equals(boolean.class
                        ))
                        {
                            String name = field.getName();
                            boolean value = field.getBoolean(this);

                            fileWriter.write(name);

                            fileWriter.write(" = ");

                            if (value == true)
                            {
                                fileWriter.write("1\n");
                            } else
                            {
                                fileWriter.write("0\n");
                            }
                        } else if (fieldClass.equals(StringProperty.class
                        ))
                        {
                            String name = field.getName();
                            StringProperty value = (StringProperty) field.get(this);

                            fileWriter.write(name);

                            fileWriter.write(
                                " = ");
                            fileWriter.write(value.get());
                            fileWriter.write(
                                "\n");
                        } else if (fieldClass.equals(IntegerProperty.class))
                        {
                            String name = field.getName();
                            IntegerProperty value = (IntegerProperty) field.get(this);

                            fileWriter.write(name);

                            fileWriter.write(" = ");
                            fileWriter.write(value.asString().get());
                            fileWriter.write("\n");
                        } else if (fieldClass.equals(FloatProperty.class))
                        {
                            String name = field.getName();
                            FloatProperty value = (FloatProperty) field.get(this);

                            fileWriter.write(name);

                            fileWriter.write(" = ");
                            fileWriter.write(LOCAL_numberFormatter.format(value.get()));
                            fileWriter.write("\n");
                        } else if (fieldClass.equals(ObservableList.class))
                        {
                            StringBuilder sb = new StringBuilder();

                            sb.append(field.getName());
                            sb.append(" = ");
                            ObservableList fieldValue = (ObservableList) field.get(this);

                            int length = fieldValue.size();

                            for (int i = 0;
                                i < length;
                                i++)
                            {
                                Object arrayElement = fieldValue.get(i);
                                if (arrayElement instanceof IntegerProperty)
                                {
                                    sb.append(((IntegerProperty) arrayElement).get());
                                } else if (arrayElement instanceof FloatProperty)
                                {
                                    sb.append(LOCAL_numberFormatter.format(((FloatProperty) arrayElement).get()));
                                } else if (arrayElement instanceof StringProperty)
                                {
                                    sb.append(((StringProperty) arrayElement).get());
                                } else if (arrayElement instanceof BooleanProperty)
                                {
                                    if (((BooleanProperty) arrayElement).get() == true)
                                    {
                                        sb.append("1");
                                    } else
                                    {
                                        sb.append("0");
                                    }
                                }
                                if (i < (length - 1))
                                {
                                    sb.append(",");
                                }
                            }

                            sb.append("\n");
                            fileWriter.write(sb.toString());
                        } else if (fieldClass.equals(BooleanProperty.class
                        ))
                        {
                            String name = field.getName();
                            BooleanProperty value = (BooleanProperty) field.get(this);

                            fileWriter.write(name);

                            fileWriter.write(
                                " = ");
                            if (value.get()
                                == true)
                            {
                                fileWriter.write("1");
                            } else
                            {
                                fileWriter.write("0");
                            }

                            fileWriter.write(
                                "\n");
                        } else
                        {
//                field.setAccessible(true);

                            String name = field.getName();
                            Object value = field.get(this);

                            fileWriter.write(name);
                            fileWriter.write(" = ");
                            fileWriter.write(value.toString());
                            fileWriter.write("\n");
                        }
                    }
                } catch (IllegalAccessException ex)
                {
                    LOCAL_steno.error("Error whilst outputting setting " + field.getName());
                }
            }

            fileWriter.close();
        } catch (IOException ex)
        {
            LOCAL_steno.error("Error creating settings file " + ex);
        }
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.writeFloat(filament_diameter.get());

        out.writeUTF(print_center.get());
        for (IntegerProperty sizeProp : bed_size)
        {
            out.writeInt(sizeProp.get());
        }
        out.writeFloat(z_offset.get());
        out.writeUTF(gcode_flavor.get());
        out.writeBoolean(use_relative_e_distances.get());
        out.writeInt(vibration_limit.get());
        out.writeUTF(end_gcode.get());
        out.writeUTF(layer_gcode.get());
        out.writeUTF(toolchange_gcode.get());

        for (FloatProperty retract_before_travelProp : retract_before_travel)
        {
            out.writeFloat(retract_before_travelProp.get());
        }
        for (FloatProperty retract_lengthProp : retract_length)
        {
            out.writeFloat(retract_lengthProp.get());
        }
        for (FloatProperty retract_length_toolchangeProp : retract_length_toolchange)
        {
            out.writeFloat(retract_length_toolchangeProp.get());
        }
        for (FloatProperty retract_liftProp : retract_lift)
        {
            out.writeFloat(retract_liftProp.get());
        }
        for (FloatProperty retract_restart_extraProp : retract_restart_extra)
        {
            out.writeFloat(retract_restart_extraProp.get());
        }
        for (FloatProperty retract_restart_extra_toolchangeProp : retract_restart_extra_toolchange)
        {
            out.writeFloat(retract_restart_extra_toolchangeProp.get());
        }
        for (IntegerProperty retract_speedProp : retract_speed)
        {
            out.writeInt(retract_speedProp.get());
        }

        for (BooleanProperty retract_layer_change_prop : retract_layer_change)
        {
            out.writeBoolean(retract_layer_change_prop.get());
        }

        for (IntegerProperty wipeProp : wipe)
        {
            out.writeInt(wipeProp.get());
        }
        for (FloatProperty nozzleDiameterProp : nozzle_diameter)
        {
            out.writeFloat(nozzleDiameterProp.get());
        }
        out.writeInt(perimeter_acceleration.get());
        out.writeInt(infill_acceleration.get());
        out.writeInt(bridge_acceleration.get());
        out.writeInt(default_acceleration.get());
        for (FloatProperty nozzlePreejectionVolumeProp : nozzle_preejection_volume)
        {
            out.writeFloat(nozzlePreejectionVolumeProp.get());
        }
        for (FloatProperty nozzleEjectionVolumeProp : nozzle_ejection_volume)
        {
            out.writeFloat(nozzleEjectionVolumeProp.get());
        }
        for (FloatProperty nozzlePartialOpenProp : nozzle_partial_b_minimum)
        {
            out.writeFloat(nozzlePartialOpenProp.get());
        }
        for (FloatProperty nozzleWipeProp : nozzle_wipe_volume)
        {
            out.writeFloat(nozzleWipeProp.get());
        }
        out.writeBoolean(infill_only_where_needed.get());
        out.writeInt(solid_infill_every_layers.get());
        out.writeInt(fill_angle.get());
        out.writeInt(solid_infill_below_area.get());
        out.writeBoolean(only_retract_when_crossing_perimeters.get());
        out.writeBoolean(infill_first.get());
        out.writeBoolean(cooling.get());
        out.writeBoolean(fan_always_on.get());
        out.writeInt(max_fan_speed.get());
        out.writeInt(min_fan_speed.get());
        out.writeInt(bridge_fan_speed.get());
        out.writeInt(disable_fan_first_layers.get());
        out.writeInt(fan_below_layer_time.get());
        out.writeInt(slowdown_below_layer_time.get());
        out.writeInt(min_print_speed.get());
        out.writeBoolean(avoid_crossing_perimeters.get());
        out.writeFloat(bridge_flow_ratio.get());
        out.writeInt(brim_width.get());
        out.writeBoolean(complete_objects.get());
        out.writeBoolean(external_perimeters_first.get());
        out.writeBoolean(extra_perimeters.get());
        out.writeInt(extruder_clearance_height.get());
        out.writeInt(extruder_clearance_radius.get());
        out.writeUTF(extrusion_axis.get());
        out.writeFloat(extrusion_width.get());
        out.writeFloat(first_layer_extrusion_width.get());
        out.writeFloat(perimeter_extrusion_width.get());
        out.writeFloat(infill_extrusion_width.get());
        out.writeFloat(solid_infill_extrusion_width.get());
        out.writeFloat(top_infill_extrusion_width.get());
        out.writeFloat(support_material_extrusion_width.get());
        out.writeFloat(first_layer_height.get());
        out.writeInt(g0.get());
        out.writeInt(gcode_arcs.get());
        out.writeBoolean(gcode_comments.get());
        out.writeInt(infill_extruder.get());
        out.writeInt(min_skirt_length.get());
        out.writeUTF(notes.get());
        out.writeUTF(output_filename_format.get());
        out.writeInt(perimeter_extruder.get());
        out.writeUTF(post_process.get());
        out.writeBoolean(randomize_start.get());
        out.writeInt(resolution.get());
        out.writeInt(rotate.get());
        out.writeInt(scale.get());
        out.writeInt(skirt_distance.get());
        out.writeInt(skirt_height.get());
        out.writeInt(skirts.get());
        out.writeUTF(solid_fill_pattern.get());
        out.writeInt(threads.get());
        out.writeInt(support_material_interface_layers.get());
        out.writeInt(support_material_interface_spacing.get());
        out.writeInt(raft_layers.get());
        out.writeInt(travel_speed.get());
        out.writeUTF(start_gcode.get());
        out.writeFloat(fill_density.get());
        out.writeUTF(fill_pattern.get());
        out.writeInt(infill_every_layers.get());
        out.writeInt(bottom_solid_layers.get());
        out.writeInt(top_solid_layers.get());
        out.writeBoolean(support_material.get());
        out.writeInt(support_material_threshold.get());
        out.writeInt(support_material_enforce_layers.get());
        out.writeUTF(support_material_pattern.get());
        out.writeFloat(support_material_spacing.get());
        out.writeInt(support_material_angle.get());
        out.writeFloat(layer_height.get());
        out.writeInt(perimeter_speed.get());
        out.writeInt(small_perimeter_speed.get());
        out.writeInt(external_perimeter_speed.get());
        out.writeInt(infill_speed.get());
        out.writeInt(solid_infill_speed.get());
        out.writeInt(top_solid_infill_speed.get());
        out.writeInt(support_material_speed.get());
        out.writeInt(bridge_speed.get());
        out.writeInt(gap_fill_speed.get());
        out.writeInt(first_layer_speed.get());
        out.writeBoolean(spiral_vase.get());
        out.writeInt(perimeters.get());
        out.writeBoolean(overhangs.get());
        out.writeInt(support_material_extruder.get());
        out.writeInt(support_material_interface_extruder.get());
        out.writeInt(first_layer_acceleration.get());
        out.writeBoolean(autowipe.get());

        /*
         * Introduced in Slic3r 1.1.4
         */
        out.writeBoolean(dont_support_bridges.get());
        out.writeInt(interface_shells.get());
        out.writeUTF(seam_position.get());
        out.writeFloat(standby_temperature_delta.get());
        out.writeInt(support_material_interface_speed.get());
        out.writeInt(force_nozzle_on_first_layer.get());
        out.writeInt(perimeter_nozzle.get());
        out.writeInt(support_nozzle.get());
        out.writeInt(support_interface_nozzle.get());
        out.writeInt(fill_nozzle.get());
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        filament_diameter = new SimpleFloatProperty(in.readFloat());

        print_center = new SimpleStringProperty(in.readUTF());
        bed_size = FXCollections.observableArrayList(new SimpleIntegerProperty(in.readInt()), new SimpleIntegerProperty(in.readInt()));
        z_offset = new SimpleFloatProperty(in.readFloat());
        gcode_flavor = new SimpleStringProperty(in.readUTF());
        use_relative_e_distances = new SimpleBooleanProperty(in.readBoolean());
        vibration_limit = new SimpleIntegerProperty(in.readInt());
        end_gcode = new SimpleStringProperty(in.readUTF());
        layer_gcode = new SimpleStringProperty(in.readUTF());
        toolchange_gcode = new SimpleStringProperty(in.readUTF());

        retract_before_travel = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_length = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_length_toolchange = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_lift = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_restart_extra = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_restart_extra_toolchange = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        retract_speed = FXCollections.observableArrayList(new SimpleIntegerProperty(in.readInt()), new SimpleIntegerProperty(in.readInt()));

        retract_layer_change = FXCollections.observableArrayList(new SimpleBooleanProperty(in.readBoolean()), new SimpleBooleanProperty(in.readBoolean()));
        wipe = FXCollections.observableArrayList(new SimpleIntegerProperty(in.readInt()), new SimpleIntegerProperty(in.readInt()));
        nozzle_diameter = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        perimeter_acceleration = new SimpleIntegerProperty(in.readInt());
        infill_acceleration = new SimpleIntegerProperty(in.readInt());
        bridge_acceleration = new SimpleIntegerProperty(in.readInt());
        default_acceleration = new SimpleIntegerProperty(in.readInt());

        nozzle_preejection_volume = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        nozzle_ejection_volume = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        nozzle_partial_b_minimum = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));
        nozzle_wipe_volume = FXCollections.observableArrayList(new SimpleFloatProperty(in.readFloat()), new SimpleFloatProperty(in.readFloat()));

        infill_only_where_needed = new SimpleBooleanProperty(in.readBoolean());
        solid_infill_every_layers = new SimpleIntegerProperty(in.readInt());
        fill_angle = new SimpleIntegerProperty(in.readInt());
        solid_infill_below_area = new SimpleIntegerProperty(in.readInt());
        only_retract_when_crossing_perimeters = new SimpleBooleanProperty(in.readBoolean());
        infill_first = new SimpleBooleanProperty(in.readBoolean());
        cooling = new SimpleBooleanProperty(in.readBoolean());
        fan_always_on = new SimpleBooleanProperty(in.readBoolean());
        max_fan_speed = new SimpleIntegerProperty(in.readInt());
        min_fan_speed = new SimpleIntegerProperty(in.readInt());
        bridge_fan_speed = new SimpleIntegerProperty(in.readInt());
        disable_fan_first_layers = new SimpleIntegerProperty(in.readInt());
        fan_below_layer_time = new SimpleIntegerProperty(in.readInt());
        slowdown_below_layer_time = new SimpleIntegerProperty(in.readInt());
        min_print_speed = new SimpleIntegerProperty(in.readInt());
        avoid_crossing_perimeters = new SimpleBooleanProperty(in.readBoolean());
        bridge_flow_ratio = new SimpleFloatProperty(in.readFloat());
        brim_width = new SimpleIntegerProperty(in.readInt());
        complete_objects = new SimpleBooleanProperty(in.readBoolean());
        external_perimeters_first = new SimpleBooleanProperty(in.readBoolean());
        extra_perimeters = new SimpleBooleanProperty(in.readBoolean());
        extruder_clearance_height = new SimpleIntegerProperty(in.readInt());
        extruder_clearance_radius = new SimpleIntegerProperty(in.readInt());
        extrusion_axis = new SimpleStringProperty(in.readUTF());

        extrusion_width = new SimpleFloatProperty(in.readFloat());
        first_layer_extrusion_width = new SimpleFloatProperty(in.readFloat());
        perimeter_extrusion_width = new SimpleFloatProperty(in.readFloat());
        infill_extrusion_width = new SimpleFloatProperty(in.readFloat());
        solid_infill_extrusion_width = new SimpleFloatProperty(in.readFloat());
        top_infill_extrusion_width = new SimpleFloatProperty(in.readFloat());
        support_material_extrusion_width = new SimpleFloatProperty(in.readFloat());

        first_layer_height = new SimpleFloatProperty(in.readFloat());
        g0 = new SimpleIntegerProperty(in.readInt());
        gcode_arcs = new SimpleIntegerProperty(in.readInt());
        gcode_comments = new SimpleBooleanProperty(in.readBoolean());
        infill_extruder = new SimpleIntegerProperty(in.readInt());
        min_skirt_length = new SimpleIntegerProperty(in.readInt());
        notes = new SimpleStringProperty(in.readUTF());
        output_filename_format = new SimpleStringProperty(in.readUTF());
        perimeter_extruder = new SimpleIntegerProperty(in.readInt());
        post_process = new SimpleStringProperty(in.readUTF());
        randomize_start = new SimpleBooleanProperty(in.readBoolean());
        resolution = new SimpleIntegerProperty(in.readInt());
        rotate = new SimpleIntegerProperty(in.readInt());
        scale = new SimpleIntegerProperty(in.readInt());
        skirt_distance = new SimpleIntegerProperty(in.readInt());
        skirt_height = new SimpleIntegerProperty(in.readInt());
        skirts = new SimpleIntegerProperty(in.readInt());
        solid_fill_pattern = new SimpleStringProperty(in.readUTF());
        threads = new SimpleIntegerProperty(in.readInt());
        support_material_interface_layers = new SimpleIntegerProperty(in.readInt());
        support_material_interface_spacing = new SimpleIntegerProperty(in.readInt());
        raft_layers = new SimpleIntegerProperty(in.readInt());
        travel_speed = new SimpleIntegerProperty(in.readInt());
        start_gcode = new SimpleStringProperty(in.readUTF());

        fill_density = new SimpleFloatProperty(in.readFloat());
        fill_pattern = new SimpleStringProperty(in.readUTF());
        infill_every_layers = new SimpleIntegerProperty(in.readInt());
        bottom_solid_layers = new SimpleIntegerProperty(in.readInt());
        top_solid_layers = new SimpleIntegerProperty(in.readInt());
        support_material = new SimpleBooleanProperty(in.readBoolean());
        support_material_threshold = new SimpleIntegerProperty(in.readInt());
        support_material_enforce_layers = new SimpleIntegerProperty(in.readInt());
        support_material_pattern = new SimpleStringProperty(in.readUTF());
        support_material_spacing = new SimpleFloatProperty(in.readFloat());
        support_material_angle = new SimpleIntegerProperty(in.readInt());
        layer_height = new SimpleFloatProperty(in.readFloat());
        perimeter_speed = new SimpleIntegerProperty(in.readInt());
        small_perimeter_speed = new SimpleIntegerProperty(in.readInt());
        external_perimeter_speed = new SimpleIntegerProperty(in.readInt());
        infill_speed = new SimpleIntegerProperty(in.readInt());
        solid_infill_speed = new SimpleIntegerProperty(in.readInt());
        top_solid_infill_speed = new SimpleIntegerProperty(in.readInt());
        support_material_speed = new SimpleIntegerProperty(in.readInt());
        bridge_speed = new SimpleIntegerProperty(in.readInt());
        gap_fill_speed = new SimpleIntegerProperty(in.readInt());
        first_layer_speed = new SimpleIntegerProperty(in.readInt());
        spiral_vase = new SimpleBooleanProperty(in.readBoolean());
        perimeters = new SimpleIntegerProperty(in.readInt());

        overhangs = new SimpleBooleanProperty(in.readBoolean());
        support_material_extruder = new SimpleIntegerProperty(in.readInt());
        support_material_interface_extruder = new SimpleIntegerProperty(in.readInt());
        first_layer_acceleration = new SimpleIntegerProperty(in.readInt());
        autowipe = new SimpleBooleanProperty(in.readBoolean());

        /*
         * Introduced in Slic3r 1.1.4
         */
        try
        {
            dont_support_bridges = new SimpleBooleanProperty(in.readBoolean());
            interface_shells = new SimpleIntegerProperty(in.readInt());
            seam_position = new SimpleStringProperty(in.readUTF());
            standby_temperature_delta = new SimpleFloatProperty(in.readFloat());
            support_material_interface_speed = new SimpleIntegerProperty(in.readInt());
            force_nozzle_on_first_layer = new SimpleIntegerProperty(in.readInt());
            perimeter_nozzle = new SimpleIntegerProperty(in.readInt());
            support_nozzle = new SimpleIntegerProperty(in.readInt());
            support_interface_nozzle = new SimpleIntegerProperty(in.readInt());
            fill_nozzle = new SimpleIntegerProperty(in.readInt());
        } catch (IOException ex)
        {
            LOCAL_steno.warning("Variables missing from config file - using defaults");
        }
    }

    private void readObjectNoData()
        throws ObjectStreamException
    {

    }

    /**
     *
     * @return
     */
    @Override
    public RoboxProfile clone()
    {
        RoboxProfile clone = new RoboxProfile();
        clone.getProfileNameProperty().set(getProfileName());
        clone.filament_diameter.set(filament_diameter.get());
        clone.print_center.set(print_center.get());

        Field[] originFields = this.getClass().getDeclaredFields();
        if (originFields.length == 0)
        {
            originFields = this.getClass().getSuperclass().getDeclaredFields();
        }

        Field[] cloneFields = clone.getClass().getDeclaredFields();
        if (cloneFields.length == 0)
        {
            cloneFields = clone.getClass().getSuperclass().getDeclaredFields();
        }

        try
        {
            Method observableListGet = List.class.getDeclaredMethod("get", int.class);

            for (int i = 0; i < originFields.length; i++)
            {
                Field originField = originFields[i];
                Field cloneField = cloneFields[i];

                try
                {
                    Class<?> fieldClass = originField.getType();

                    if (originField.getName().startsWith("LOCAL") == false)
                    {
                        if (fieldClass.isArray())
                        {
                            Object originFieldValue = originField.get(this);
                            Object cloneFieldValue = cloneField.get(clone);

                            int length = Array.getLength(originFieldValue);
                            for (int arrayIndex = 0; arrayIndex < length; arrayIndex++)
                            {
                                Array.set(cloneFieldValue, arrayIndex, Array.get(originFieldValue, arrayIndex));
                            }
                        } else if (fieldClass.equals(boolean.class))
                        {
                            cloneField.setBoolean(clone, originField.getBoolean(this));
                        } else if (fieldClass.equals(StringProperty.class))
                        {
                            cloneField.set(clone, originField.get(this));
                        } else if (fieldClass.equals(IntegerProperty.class))
                        {
                            cloneField.set(clone, originField.get(this));
                        } else if (fieldClass.equals(FloatProperty.class))
                        {
                            cloneField.set(clone, originField.get(this));
                        } else if (fieldClass.equals(ObservableList.class))
                        {
                            Type genericType = originField.getGenericType();
                            Class<?> fieldContentClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];

                            ObservableList originFieldValue = (ObservableList) originField.get(this);
                            ObservableList cloneFieldValue = (ObservableList) cloneField.get(this);

                            int length = originFieldValue.size();
                            for (int index = 0;
                                index < length;
                                index++)
                            {
                                Object originArrayElement = originFieldValue.get(index);
                                Object cloneArrayElement = cloneFieldValue.get(index);

                                try
                                {
                                    if (fieldContentClass.equals(IntegerProperty.class))
                                    {
                                        IntegerProperty property = (IntegerProperty) observableListGet.invoke(cloneField.get(clone), index);
                                        property.set(((IntegerProperty) originArrayElement).get());
                                    } else if (fieldContentClass.equals(FloatProperty.class))
                                    {
                                        FloatProperty property = (FloatProperty) observableListGet.invoke(cloneField.get(clone), index);
                                        property.set(((FloatProperty) originArrayElement).get());
                                    } else if (fieldContentClass.equals(StringProperty.class))
                                    {
                                        StringProperty property = (StringProperty) observableListGet.invoke(cloneField.get(clone), index);
                                        property.set(((StringProperty) originArrayElement).get());
                                    }
                                } catch (InvocationTargetException ex)
                                {
                                    LOCAL_steno.error("Couldn't set up field " + originField.getName() + " " + ex);
                                }
                            }
                        } else if (fieldClass.equals(BooleanProperty.class))
                        {
                            cloneField.set(clone, originField.get(this));
                        } else
                        {
                            cloneField.set(clone, originField.get(this));
                        }
                    }
                } catch (IllegalAccessException ex)
                {
                    LOCAL_steno.error("Error whilst outputting setting " + originField.getName());
                }
            }
        } catch (NoSuchMethodException ex)
        {
            LOCAL_steno.error("Couldn't establish reflection methods whilst setting up clone " + ex);
        }

        return clone;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        return LOCAL_profileName.get();
    }
    
    public int getVersionNumber()
    {
        return LOCAL_version_number;
    }

    public void setVersionNumber(int versionNumber)
    {
        LOCAL_version_number = versionNumber;
    }
}
