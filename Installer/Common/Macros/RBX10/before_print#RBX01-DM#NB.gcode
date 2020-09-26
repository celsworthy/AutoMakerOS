M83					;Set Extruder to Relative moves
M139				;Set & heat first layer Bed temp.
M170				;Set Ambient temp.

Macro:Home_all_Axis_in_sequence

M190 S T P43		;Wait for right time to start heating nozzles; 43 secs allowance for short purges + level gantry + bed
M103				;Set & heat first layer nozzle temp.

T0
M109				;Wait for Nozzle to get to temp.
T1
M109				;Wait for Nozzle to get to temp.

Macro:Level_Gantry_(2-points)

Macro:7_point_Bed_probing-Set_Washout

Macro:Short_Purge#RBX01-DM#N0

Macro:Short_Purge#RBX01-DM#N1

G0 X150 Y105 Z5		;Centre head and prime
M190				;Wait for bed to reach target temp (a precaution - it should have got there by now)
G1 E3 D3 F500		;Prime
M129				;Head LED on