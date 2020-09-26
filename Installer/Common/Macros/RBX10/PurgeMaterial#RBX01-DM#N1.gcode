M83					;Set Extruder to Relative moves

Macro:Home_all_Axis_in_sequence

M190 S				;Wait for right time to start heating nozzles
M109				;wait to get to nozzle temp

Macro:Level_Gantry_(2-points)

Macro:7_point_Bed_probing-Set_Washout

M129				;Head LED on
M106				;Fan on

G36 E1500 F400		;Un-Park

G0 X280 Y20
Macro:Purge_T1
G0 X280 Y24
Macro:Purge_T1
G0 X280 Y28
Macro:Purge_T1
G0 X280 Y32
Macro:Purge_T1
G0 X280 Y36
Macro:Purge_T1

Macro:Finish-Abort_Print