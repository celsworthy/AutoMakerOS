M83					;Set Extruder to Relative moves
M140 E				;Set post layer 1 target from reel.
M139 E				;Set & heat first layer Bed temp.
M170 E				;Set Ambient temp.

Macro:Home_all_Axis_in_sequence

M190 T P43			;Wait for right time to start heating nozzle; 43 secs allowance for short purges + level gantry + bed 
M103 T				;Set & heat first layer nozzle temp.
T1
M109				;Wait for Nozzle to get to temp.

Macro:Level_Gantry_(2-points)

Macro:7_point_Bed_probing-Set_Washout

Macro:Short_Purge

G0 X105 Y75 Z5		;Centre head and prime
M190				;Wait for bed to reach target temp (a precaution - it should have got there by now)
G1 E0.5 F400		;Prime
M129				;Head LED on