Macro:Home_all_Axis_in_sequence

M190 S T			;Wait for right time to start heating nozzles
M109				;wait to get to nozzle temp

Macro:Level_Gantry_(2-points)

M129				;Head LED on
M106				;Fan on

G36 E1500 F400		;Un-Park
G36 D1500 F400 		;Un-Park

G0 X20 Y20
Macro:Purge_T0
G0 Y24
Macro:Purge_T1
G0 Y28
Macro:Purge_T0
G0 Y32
Macro:Purge_T1
G0 Y36
Macro:Purge_T0
G0 Y40
Macro:Purge_T1
G0 Y44
Macro:Purge_T0
G0 Y48
Macro:Purge_T1
G0 Y52
Macro:Purge_T0
G0 Y56
Macro:Purge_T1

Macro:Finish-Abort_Print