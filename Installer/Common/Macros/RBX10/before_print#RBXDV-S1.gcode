M83					;Set Extruder to Relative moves
M139				;Set & heat first layer Bed temp.
M170				;Set Ambient temp.

Macro:Home_all_Axis_in_sequence

M190 S P43			;Wait for right time to start heating nozzles; 43 secs allowance for short purges + level gantry + bed 
G0 Y-1 X0 Z1		;Move to start of short purge
M103				;Set & heat first layer nozzle temp.
M109				;Wait for Nozzle to get to temp.

M129				;Head LED on

Macro:Level_Gantry_(2-points)

Macro:7_point_Bed_probing-Set_Washout

G0 Y-1 X0 Z1		;Move to start of short purge
G0 E25				;Un-retract

Macro:Short_Purge#RBXDV-S1

M190				;Wait for bed to reach target temp (a precaution - it should have got there by now)
G0 E25				;Un-retract