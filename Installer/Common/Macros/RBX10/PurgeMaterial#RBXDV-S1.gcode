M83					;Set Extruder to Relative moves

Macro:Home_all_Axis_in_sequence

M190 S				;Wait for right time to start heating nozzle
M109				;wait to get to nozzle temp

Macro:Level_Gantry_(2-points)

Macro:7_point_Bed_probing-Set_Washout

G0 E-25				;Retract

M129				;Head LED on
M106				;Fan on

G0 X20 Y20
G36 E1500 F1200 	;Un-Park
Macro:Purge_T0
G0 X20 Y24
Macro:Purge_T0
G0 X20 Y28
Macro:Purge_T0
G0 X20 Y32
Macro:Purge_T0
G0 X20 Y36
Macro:Purge_T0

Macro:Finish-Abort_Print